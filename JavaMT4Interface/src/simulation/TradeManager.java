/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.HashMap;
import java.util.List;
//
/**
 *
 * @author Moohdyy
 */
public class TradeManager {

    private final HashMap<Integer, Trade> allTrades;
    private int counter;
    private double balance = 0;
    private double equity = 0;
    private double usedMargin = 0;
    private double leverage = 1;

    public TradeManager() {
        this.counter = 0;
        this.allTrades = new HashMap<>();
    }

    TradeManager(double balance) {
        this();
        this.balance = balance;
        this.equity = balance;
    }

    TradeManager(double balance, double leverage) {
        this(balance);
        this.leverage = leverage;
    }

    public void processTrades(List<Trade> tradeList, double bid, double ask, long timeStamp) {
        for (Trade tradeInList : tradeList) {
            Trade actualTrade;
            int javaIDtradeInList = tradeInList.getJavaID();
            if (javaIDtradeInList == -1) { //new Trade, was not in List before
                javaIDtradeInList = newOrder(tradeInList, timeStamp, bid, ask);
                copyValuesFromNewTrade(tradeInList, getTradeByID(javaIDtradeInList));
            }
            actualTrade = getTradeByID(javaIDtradeInList);
            if (actualTrade.isOpen()) {
                if (!tradeInList.isOpen()) { // order has to be closed
                    closeOrder(javaIDtradeInList, ask);
                    tradeInList.close();
                }
            }
        }

    }

    public int newOrder(Trade tradeToPlaceOrder, long timeStamp, double bid, double ask) {
        double bidOrAsk = getBuyOrSellingPrice(tradeToPlaceOrder.getTradeType(), bid, ask);
        Trade trade = new Trade(tradeToPlaceOrder.getTradeType(), tradeToPlaceOrder.getVolume());
        trade.setStopLoss(tradeToPlaceOrder.getStopLoss());
        trade.setTakeProfit(tradeToPlaceOrder.getTakeProfit());
        trade.setTimeStampOpen(timeStamp);
        trade.setOpeningPrice(bidOrAsk);
        trade.setJavaID(counter);
        StrategySimulation.writeToLogFileAndOutput(String.format("--- Order %3d (" + trade.getTradeTypeName() + ") opened at %6f with volume: " + trade.getVolume() + " ---", trade.getJavaID(), bidOrAsk));
        allTrades.put(counter, trade);
        double margin = getMarginAmountForOneTrade(trade.getVolume());
        this.usedMargin += margin;
        return counter++;
    }

    public void closeTrade(Trade trade, double closingPrice) {
        if (trade.isOpen()) {
//            double profitOrLoss = trade.getProfitOrLoss(closingPrice);
            double profitOrLoss = trade.getProfitOrLossOld(closingPrice);
            StrategySimulation.writeToLogFileAndOutput(String.format("--- Trade %3d (" + trade.getTradeTypeName() + ") closed at %6f, profit/loss: " + profitOrLoss, trade.getJavaID(), closingPrice));
            balance += profitOrLoss;
            usedMargin -= trade.getVolume() / getLeverage();
            trade.close();
        } else {
            throw new TradeException("Order was already closed");
        }
    }

    public double getBuyOrSellingPrice(int orderType, double bid, double ask) {
        if (orderType == Trade.BUY) {
            return ask;
        } else {
            return bid;
        }
    }

    public void closeOrder(Integer tradeID, double actualPrice) {
        Trade tradeToClose = allTrades.get(tradeID);
        closeTrade(tradeToClose, actualPrice);
    }

    public void closeAllOrders(double actualPrice) {
        for (Trade trade : this.allTrades.values()) {
            if (trade.isOpen()) {
                closeTrade(trade, actualPrice);
            }
        }
    }

    public boolean checkForMarginCall() {
        return equity <= getUsedMargin();
    }

    public double getActualTotalProfitOrLoss(double actualPrice) {
        double ret = 0;
        for (Trade trade : allTrades.values()) {
            if (trade.isOpen()) {
                ret += trade.getProfitOrLoss(actualPrice);
            }
        }
        return ret;
    }

    public Trade getTradeByID(int id) {
        return this.allTrades.get(id);

    }

    public int getActiveTradesCount() {
        int ret = 0;
        for (Trade trade : allTrades.values()) {
            if (trade.isOpen()) {
                ret++;
            }
        }
        return ret;
    }

    public int getClosedTradesCount() {
        int ret = 0;
        for (Trade trade : allTrades.values()) {
            if (!trade.isOpen()) {
                ret++;
            }
        }
        return ret;
    }

    double getMarginAmountForOneTrade(double volume) {
        return volume / leverage;
    }

    public double getBalance() {
        return this.balance;
    }

    public double getEquity() {
        return equity;
    }

    /**
     * @return the leverage
     */
    public double getLeverage() {
        return leverage;
    }

    /**
     * @param leverage the leverage to set
     */
    public void setLeverage(double leverage) {
        this.leverage = leverage;
    }

    public void checkStopLossTakeProfit(double actualPrice) {
        for (Trade trade : allTrades.values()) {
            if (trade.isOpen()) {
                switch (trade.getTradeType()) {
                    case Trade.BUY:
                        if (trade.hasStopLoss()) {
                            if (actualPrice <= trade.getStopLoss()) {
                                closeTrade(trade, actualPrice);
                            }
                        }
                        if (trade.hasTakeProfit()) {
                            if (actualPrice >= trade.getTakeProfit()) {
                                closeTrade(trade, actualPrice);
                            }
                        }
                        break;
                    case Trade.SELL:
                        if (trade.hasStopLoss()) {
                            if (actualPrice >= trade.getStopLoss()) {
                                closeTrade(trade, actualPrice);
                            }
                        }
                        if (trade.hasTakeProfit()) {
                            if (actualPrice <= trade.getTakeProfit()) {
                                closeTrade(trade, actualPrice);
                            }
                        }
                        break;
                }
            }
        }
    }

    public double getUsableMargin() {
        return equity - getUsedMargin();
    }

    private void copyValuesFromNewTrade(Trade tradeWhichValuesAreToBeActualized, Trade newTrade) {
        tradeWhichValuesAreToBeActualized.setJavaID(newTrade.getJavaID());
        tradeWhichValuesAreToBeActualized.setOpeningPrice(newTrade.getOpeningPrice());
        tradeWhichValuesAreToBeActualized.setTimeStampOpen(newTrade.getTimeStampOpen());
    }

    /**
     * @return the usedMargin
     */
    public double getUsedMargin() {
        return usedMargin;
    }

    void calculateNewEquity(double actualPrice) {
        this.equity = this.balance + getActualTotalProfitOrLoss(actualPrice);
    }
}

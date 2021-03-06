/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import datacollection.CurrencyCourseOHLC;
import datacollection.OHLC;

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
    private double leverage = 0.2;
    private BufferedWriter tradeWriter;
    public 
    TradeManager() {
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
                	double value=bid;
                	if(tradeInList.getTradeType()==Trade.SELL)
                	{
                		value=ask;
                	}
                    closeOrder(javaIDtradeInList,timeStamp, value);
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
        trade.setPattern(tradeToPlaceOrder.getPattern());
        trade.setJavaID(counter);
        
        StrategySimulation.writeToLogFileAndOutput(String.format("--- Order %3d (" + trade.getTradeTypeName() + ") opened at %6f with volume: " + trade.getVolume() + " ---", trade.getJavaID(), bidOrAsk));
        allTrades.put(counter, trade);
        double margin = getMarginAmountForOneTrade(trade.getVolume());
        this.usedMargin += margin;
        return counter++;
    }

    public void closeTrade(Trade trade, long timestamp, double closingPrice) {
        if (trade.isOpen()) {
//            double profitOrLoss = trade.getProfitOrLoss(closingPrice);
            double profitOrLoss = trade.getProfitOrLossOld(closingPrice);
            trade.setTimeStampClose(timestamp);
            StrategySimulation.writeToLogFileAndOutput(String.format("--- Trade %3d (" + trade.getTradeTypeName() + ") closed at %6f, profit/loss: " + profitOrLoss, trade.getJavaID(), closingPrice));
            balance += profitOrLoss;
            usedMargin -= trade.getVolume() / getLeverage();
            trade.close();
            if(tradeWriter!=null)
            {
            try {
				tradeWriter.write(trade.toString()+System.lineSeparator());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        } else {
            throw new TradeException("Order was already closed");
        }
    }
    public void setTradeWriter(BufferedWriter bw)
    {
    	tradeWriter=bw;
    }
    public double getBuyOrSellingPrice(int orderType, double bid, double ask) {
        if (orderType == Trade.BUY) {
            return ask;
        } else {
            return bid;
        }
    }

    public void closeOrder(Integer tradeID, long timeStamp, double actualPrice) {
    	double b=Math.random();
    	double plus=1.0;
    	if(b<0.5)
    	{
    		plus=-1.0;
    	}
    	double value=actualPrice/10000*5*Math.random();
    	
    	actualPrice+=value*plus;
        Trade tradeToClose = allTrades.get(tradeID);
        closeTrade(tradeToClose, timeStamp, actualPrice);
    }

    public void closeAllOrders(long timeStamp, double actualPrice) {
        for (Trade trade : this.allTrades.values()) {
            if (trade.isOpen()) {
                closeOrder(trade.getJavaID(), timeStamp, actualPrice);
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

    public void checkStopLossTakeProfit(CurrencyCourseOHLC cc,long timeStamp,double actualPrice) {
       OHLC c=cc.getOHLC(cc.getActualPosition());
    	for (Trade trade : allTrades.values()) {
            if (trade.isOpen()) {
                switch (trade.getTradeType()) {
                    case Trade.BUY:
                        if (trade.hasStopLoss()) {
                        	double value=c.getLow();
                            if (value <= trade.getStopLoss()) {
                                closeTrade(trade,timeStamp, trade.getStopLoss());
                            }
                        }
                        if(trade.isOpen())
                        {
                        if (trade.hasTakeProfit()) {
                        	double value=c.getHigh();
                            if (value >= trade.getTakeProfit()) {
                                closeTrade(trade,timeStamp, trade.getTakeProfit());
                            }
                        }
                        }
                        break;
                    case Trade.SELL:
                        if (trade.hasStopLoss()) {
                        	double value=c.getHigh();
                            if (value >= trade.getStopLoss()) {
                                closeTrade(trade,timeStamp, trade.getStopLoss());
                            }
                        }
                        if(trade.isOpen())
                        {
                        if (trade.hasTakeProfit()) {
                        	double value=c.getLow();
                            if (value <= trade.getTakeProfit()) {
                                closeTrade(trade, timeStamp,trade.getTakeProfit());
                            }
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

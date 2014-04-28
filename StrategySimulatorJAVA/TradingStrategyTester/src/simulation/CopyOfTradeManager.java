/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.HashMap;
import java.util.List;
import strategies.AbstractStrategy;

/**
 *
 * @author Moohdyy
 */
public class CopyOfTradeManager {

    private final HashMap<Integer, Trade> allTrades;
    private int counter;
    private double balance = 0;
    private double equity = 0;
    private double usedMargin = 0;
    private double leverage = 1;

    public CopyOfTradeManager() {
        this.counter = 0;
        this.allTrades = new HashMap<>();
    }

    CopyOfTradeManager(double balance) {
        this();
        this.balance = balance;
    }

    CopyOfTradeManager(double balance, double leverage) {
        this(balance);
        this.leverage = leverage;
    }

    public void processTrades(List<Trade> tradeList, double bid, double ask, long timeStamp) {
        for (Trade tradeInList : tradeList) {
            Trade actualTrade;
            int javaIDtradeInList = tradeInList.getJavaID();
            if (javaIDtradeInList == -1) { //new Trade, was not in List before
                javaIDtradeInList = newOrder(tradeInList.getTradeType(), tradeInList.getVolume(), timeStamp, bid, ask);
            }
            actualTrade = getTradeByID(javaIDtradeInList);
            if (actualTrade.isOpen()) {
                if (!tradeInList.isOpen()) {
                    closeOrder(javaIDtradeInList, ask);
                    tradeInList.close();
                }
            }
            tradeInList.setJavaID(javaIDtradeInList);
        }

    }

    public int newOrder(int orderType, double volume, long timeStamp, double bid, double ask) {
        double bidOrAsk = getBuyOrSellingPrice(orderType, bid, ask);
        Trade trade = new Trade(orderType, volume * this.leverage);
        trade.setTimeStampOpen(timeStamp);
        trade.setOpeningPrice(bidOrAsk);
        trade.setJavaID(counter);
        System.out.println("Trade " + trade.getJavaID() + "(" + ((trade.getTradeType() == 1) ? "buy" : "sell") + ") opened at " + bidOrAsk + " with volume: " + volume * this.leverage);
        allTrades.put(counter, trade);
        double marigin= getMargin(volume);
        this.usedMargin +=marigin;
        equity-=marigin;
        return counter++;
    }

    public double closeTrade(Trade trade, double closingPrice) {
        if (trade.isOpen()) {
            trade.close();
            double profitOrLoss = trade.getProfitOrLoss(closingPrice);
            System.out.println("Trade " + trade.getJavaID() + " closed at " + closingPrice + ", profit/loss: " + profitOrLoss);
            equity+=profitOrLoss;
            balance+=profitOrLoss;
            return profitOrLoss;
        } else {
            throw new TradeException("Order was already closed");
        }
    }

    public double getBuyOrSellingPrice(int orderType, double bid, double ask) {
        if (orderType == AbstractStrategy.BUY) {
            return ask;
        } else {
            return bid;
        }
    }

    public void closeOrder(Integer tradeID, double actualPrice) {
        Trade tradeToClose = allTrades.get(tradeID);
        this.usedMargin -= tradeToClose.getVolume() * actualPrice / getLeverage();
        balance += closeTrade(tradeToClose, actualPrice);
    }

    private void closeAllOrders(double actualPrice) {
        for (Trade trade : this.allTrades.values()) {
            if (trade.isOpen()) {
                closeTrade(trade, actualPrice);
            }
        }
    }

    public boolean checkForMarginCall() {
    	if(equity<=usedMargin)
    	{
    		return true;
    	}else{
    		return false;
    	}
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

    double getMargin(double volume) {
        //TODO
        //throw new UnsupportedOperationException("Not supported yet.");
       return volume/leverage;
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
            switch(trade.getTradeType()){
                case AbstractStrategy.BUY:
                    if(trade.hasStopLoss()){
                        if(actualPrice<=trade.getStopLoss()){
                            trade.close();
                        }
                    }
                    if(trade.hasTakeProfit()){
                        if(actualPrice>=trade.getTakeProfit()){
                            trade.close();
                        }
                    }
                    break;
                case AbstractStrategy.SELL:
                    if(trade.hasStopLoss()){
                        if(actualPrice>=trade.getStopLoss()){
                            trade.close();
                        }
                    }
                    if(trade.hasTakeProfit()){
                        if(actualPrice<=trade.getTakeProfit()){
                            trade.close();
                        }
                    }
                    break;
            }
        }
    
    }

    public double getUsableMargin() {
        return equity-usedMargin;
    }
}

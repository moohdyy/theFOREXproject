/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.HashMap;

/**
 *
 * @author Moohdyy
 */
public class TradeManager {

    private final HashMap<Integer, Trade> allTrades;
    private int counter;
    private double balance = 0;
    private double usedMargin = 0;
    private double leverage = 1;

    public TradeManager() {
        this.counter = 0;
        this.allTrades = new HashMap<>();
    }

    TradeManager(double balance) {
        this();
        this.balance = balance;
    }
    TradeManager(double balance, double leverage) {
        this(balance);
        this.leverage = leverage;
    }

    public int newOrder(int orderType, double actualPrice, double volume, long timeStamp) {
        Trade trade = new Trade(counter, orderType, actualPrice, volume*leverage, timeStamp);
        allTrades.put(counter, trade);
        this.usedMargin += volume*actualPrice/getLeverage();
        return counter++;
    }

    public void closeOrder(Integer tradeID, double actualPrice) {
        Trade tradeToClose = allTrades.get(tradeID);
        
        this.usedMargin -= tradeToClose.getVolume()*actualPrice/getLeverage();
        balance+= tradeToClose.closeTrade(actualPrice);
    }
    
    private void closeAllOrders(double actualPrice){
        for (Trade trade : this.allTrades.values()) {
            if(trade.isOpen()){
                trade.closeTrade(actualPrice);
            }
        }
    }
    
    public boolean checkForMarginCall(double actualPrice){
        if(getEquity(actualPrice)<=this.usedMargin){
            closeAllOrders(actualPrice);
            return true;
        }else{
            return false;
        }
    }

    public double getTotalProfitOrLoss(double actualPrice) {
        double ret = 0;
        for (Trade trade : allTrades.values()) {
            if (trade.isOpen()) {
                ret += trade.getProfitOrLoss(actualPrice);
            }
        }
        return ret;
    }

    public Trade getTradeByID(int id) {
        if (this.allTrades.get(id) != null) {
            return this.allTrades.get(id);
        } else {
            throw new TradeException("Trade with ID " + id + " not found");
        }
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

    double getMargin(double bidPrice, double askPrice) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double getBalance() {
        return this.balance;
    }

    public double getEquity(double actualPrice) {
         return this.balance+getTotalProfitOrLoss(actualPrice);
    }

    public double getUsedMargin() {
        return this.usedMargin;
    }
    
    public double getUseableMargin(double actualPrice){
        return getEquity(actualPrice)-this.usedMargin;
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
}

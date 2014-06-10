/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import java.util.Calendar;

/**
 *
 * @author Moohdyy
 */
public class SimulationResults {

    private String strategyName;
    private String currencyPair;
    private int year;
    private int month;
    private double finalBalance;
    private int numberOfClosedTrades;
    private int numberOfOpenTrades;
    private boolean marginCall = false;
    private static String SEPARATOR = ";";

    public SimulationResults(StrategySimulation sim) {
        this.strategyName = sim.getStrategy().getName();
        this.currencyPair = sim.getCc().getCurrencyPair();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sim.getCc().getOHLC(0).getTimestamp());
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
    }

    public SimulationResults(StrategySimulation sim, TradeManager tm) {
        this(sim);
        this.finalBalance = tm.getBalance();
        this.numberOfClosedTrades = tm.getClosedTradesCount();
        this.numberOfOpenTrades = tm.getActiveTradesCount();
    }

    public SimulationResults(StrategySimulation sim, TradeManager tm, boolean marginCall) {
        this(sim, tm);
        this.marginCall = marginCall;
    }

    public static String getHeader() {
        return "currencyPair" + SEPARATOR + "year" + SEPARATOR + "month" + SEPARATOR + "final balance" + SEPARATOR + "#trades still open" + SEPARATOR + "#trades closed" + SEPARATOR + "margin call";
    }

    @Override
    public String toString() {
        return currencyPair + SEPARATOR + year + SEPARATOR + month + SEPARATOR + Math.round(finalBalance) + SEPARATOR + numberOfOpenTrades + SEPARATOR + numberOfClosedTrades + SEPARATOR + marginCall;
    }

    /**
     * @return the finalBalance
     */
    public double getFinalBalance() {
        return finalBalance;
    }

    /**
     * @param finalBalance the finalBalance to set
     */
    public void setFinalBalance(double finalBalance) {
        this.finalBalance = finalBalance;
    }

    /**
     * @return the numberOfClosedTrades
     */
    public int getNumberOfClosedTrades() {
        return numberOfClosedTrades;
    }

    /**
     * @param numberOfTrades the numberOfClosedTrades to set
     */
    public void setNumberOfClosedTrades(int numberOfTrades) {
        this.numberOfClosedTrades = numberOfTrades;
    }

    /**
     * @return the numberOfOpenTrades
     */
    public int getNumberOfOpenTrades() {
        return numberOfOpenTrades;
    }

    /**
     * @param numberOfTrades the numberOfOpenTrades to set
     */
    public void setNumberOfOpenTrades(int numberOfTrades) {
        this.numberOfOpenTrades = numberOfTrades;
    }

    /**
     * @return the marginCall
     */
    public boolean isMarginCall() {
        return marginCall;
    }

    /**
     * @param marginCall the marginCall to set
     */
    public void setMarginCall(boolean marginCall) {
        this.marginCall = marginCall;
    }
}

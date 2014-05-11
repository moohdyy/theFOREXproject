/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import datacollection.CurrencyCourseOHLC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import strategies.AbstractStrategy;

/**
 *
 * @author Moohdyy
 */
public class CopyOfStrategySimulation {

    public static String PRINTSEPARATOR = System.lineSeparator() + "________________________________________";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static File logFile;
    private CopyOfTradeManager tm;
    private AbstractStrategy strategy;
    private CurrencyCourseOHLC cc;
    private long actualTime = 0;

    public CopyOfStrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance) {
        this.cc = cc;
        this.strategy = strategy;
        this.tm = new CopyOfTradeManager(balance);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cc.getOHLCOfActualPosition().getTimestamp());
        String startOfCC = cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH) + 1) + "_";
        CopyOfStrategySimulation.logFile = new File("output" + cc.getCurrencyPair() + "_" + strategy.getName() + "_" + startOfCC + ".txt");
        logFile.delete();
        CopyOfStrategySimulation.logFile = new File("output" + cc.getCurrencyPair() + "_" + strategy.getName() + "_" + startOfCC + ".txt");
    }

    public CopyOfStrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance, double leverage) {
        this(strategy, cc, balance);
        this.tm.setLeverage(leverage);
    }

    /**
     * simulates the strategy over the whole CurrencyCource
     *
     * @param windowInMinutes the time intervall in which the course is
     * accessible to the strategy (if windowInMinutes is 1, every OHLC is
     * analyzed by the strategy)
     */
    public void simulateStrategy(int windowInMinutes) {
        long windowInMilliseconds = windowInMinutes * 60 * 1000;
        List<Trade> trades = new ArrayList<>();
        for (int index = 0; index < cc.getNumberOfEntries(); index++) {
            double actualPrice = cc.getClose(index);
            if (checkNewPrice(actualPrice)) {
                return;
            }
            this.actualTime = cc.getTimeStamp(index);
            long windowTime = cc.getTimeStamp(cc.getActualPosition());
            if (this.actualTime >= windowTime + windowInMilliseconds) { //in our case we can access new course after window
                cc.setActualPosition(index);
                System.out.println("Strategy running at actual price of " + cc.getCurrencyPair() + " : " + actualPrice + PRINTSEPARATOR);
                trades = strategy.processNewCourse(trades, cc);
            }
            tm.processTrades(trades, cc.getBidPrice(index), cc.getClose(index), this.actualTime);
            printCurrentStats(index);
        }
    }

    private boolean checkNewPrice(double actualPrice) {
        if (tm.checkForMarginCall()) {
            tm.closeAllOrders(actualPrice);
            System.out.println("MARGIN CALL, simulation stopped.");
            return true;
        }
        tm.checkStopLossTakeProfit(actualPrice);
        tm.calculateNewEquity(actualPrice);
        return false;
    }

    public void printCurrentStats(int index) {
        double ask = cc.getAskPrice(index);
        String output = sdf.format(new Date(actualTime))
                + ":" + System.lineSeparator() + " ActualPrice:   " + ask
                + System.lineSeparator() + " ActiveTrades:  " + tm.getActiveTradesCount()
                + System.lineSeparator() + " ClosedTrades:  " + tm.getClosedTradesCount()
                + System.lineSeparator() + " Balance:       " + Math.round(this.tm.getBalance())
                + System.lineSeparator() + " Equity:        " + Math.round(this.tm.getEquity())
                + System.lineSeparator() + " Used Margin:   " + Math.round(this.tm.getUsedMargin())
                + System.lineSeparator() + " Usable Margin: " + Math.round(this.tm.getUsableMargin())
                + System.lineSeparator() + "________________________________________" + System.lineSeparator() + "";
        writeToLogFile(output);
        System.out.print(output);
    }

    public static void writeToLogFile(String text) {
        FileWriter fw;
        try {
            fw = new FileWriter(logFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text + System.lineSeparator());
            bw.close();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

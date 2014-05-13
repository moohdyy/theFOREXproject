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
import main.Start;

import strategies.AbstractStrategy;

/**
 *
 * @author Moohdyy
 */
public class StrategySimulation {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static File logFile;
    private TradeManager tm;
    private AbstractStrategy strategy;
    private CurrencyCourseOHLC cc;
    private long actualTime = 0;
    private static BufferedWriter bw;
    private FileWriter fw;

    public StrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance) {
        this.cc = cc;
        this.strategy = strategy;
        this.tm = new TradeManager(balance);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cc.getOHLCOfActualPosition().getTimestamp());
        String startOfCC = cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH) + 1);
        String filename = Start.FOLDERNAME + strategy.getName() + "\\output" + cc.getCurrencyPair() + "_" + startOfCC + ".txt";
        StrategySimulation.logFile = new File(filename);
        logFile.delete();
        StrategySimulation.logFile = new File(filename);

        try {
            fw = new FileWriter(logFile, true);
            bw = new BufferedWriter(fw);
        } catch (IOException ex) {
            Logger.getLogger(StrategySimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance, double leverage) {
        this(strategy, cc, balance);
        this.tm.setLeverage(leverage);
    }

    /**
     * simulates the strategy over the whole CurrencyCource
     *
     * @param windowInMinutes the time intervall in which the course is
     * accessible to the strategy (if windowInMinutes is 1, every OHLC is
     * analyzed by the strategy)
     * @return double teh final balance
     */
    public double simulateStrategy(int windowInMinutes) {
        long windowInMilliseconds = windowInMinutes * 60 * 1000;
        List<Trade> trades = new ArrayList<>();
        for (int index = 0; index < cc.getNumberOfEntries(); index++) {
            double actualPrice = cc.getClose(index);
            if (checkNewPrice(actualPrice)) {
                return tm.getBalance();
            }
            this.actualTime = cc.getTimeStamp(index);
            long windowTime = cc.getTimeStamp(cc.getActualPosition());
            if (this.actualTime >= windowTime + windowInMilliseconds) { //in our case we can access new course after window
                cc.setActualPosition(index);
                System.out.println("--- Strategy analyzing at actual price of: " + actualPrice + " ---");
                trades = strategy.processNewCourse(trades, cc);
            }
            tm.processTrades(trades, cc.getBidPrice(index), cc.getClose(index), this.actualTime);
            printCurrentStats(index);
        }
        try {
            bw.close();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(StrategySimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tm.getBalance();

    }

    private boolean checkNewPrice(double actualPrice) {
        if (tm.checkForMarginCall()) {
            tm.closeAllOrders(actualPrice);
            System.out.println("MARGIN CALL, simulation stopped. Balance: " + tm.getBalance());
            return true;
        }
        tm.checkStopLossTakeProfit(actualPrice);
        tm.calculateNewEquity(actualPrice);
        return false;
    }

    public void printCurrentStats(int index) {
        double ask = cc.getAskPrice(index);
        String output = String.format("%s: Course:%6f | Active/Closed Trades:%3d/%3d | Balance:%6d | Equity:%6d | Used Margin:%6d | Usable Margin:%6d", sdf.format(new Date(actualTime)), ask, tm.getActiveTradesCount(), tm.getClosedTradesCount(), Math.round(this.tm.getBalance()), Math.round(this.tm.getEquity()), Math.round(this.tm.getUsedMargin()), Math.round(this.tm.getUsableMargin()));
        writeToLogFileAndOutput(output);
    }

    public static void writeToLogFileAndOutput(String text) {
        System.out.println(text);
        try {
            bw.write(text + System.lineSeparator());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

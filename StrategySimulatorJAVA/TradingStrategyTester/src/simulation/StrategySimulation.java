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
    private static boolean writeToLogFile;

    public StrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance, boolean writeToLogFile) {
        this.cc = cc;
        this.strategy = strategy;
        this.tm = new TradeManager(balance);
        StrategySimulation.writeToLogFile = writeToLogFile;
        if (writeToLogFile) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cc.getOHLCOfActualPosition().getTimestamp());
            String startOfCC = cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH) + 1);
            String filename = Start.FOLDERNAME + strategy.getName() + "\\output" + cc.getCurrencyPair() + "_" + startOfCC + ".txt";
            StrategySimulation.logFile = new File(filename);

            try {
                fw = new FileWriter(logFile, true);
                bw = new BufferedWriter(fw);
            } catch (IOException ex) {
                Logger.getLogger(StrategySimulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public StrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance, double leverage, boolean writeToLogFile) {
        this(strategy, cc, balance, writeToLogFile);
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
    public SimulationResults simulateStrategy(int windowInMinutes) {
        long windowInMilliseconds = windowInMinutes * 60 * 1000;
        List<Trade> trades = new ArrayList<>();
        for (int index = 0; index < getCc().getNumberOfEntries(); index++) {
            double actualPrice = getCc().getClose(index);
            if (checkNewPrice(actualPrice)) {
                return new SimulationResults(this, tm, true);
            }
            this.actualTime = getCc().getTimeStamp(index);
            long windowTime = getCc().getTimeStamp(getCc().getActualPosition());
            if (this.actualTime >= windowTime + windowInMilliseconds) { //in our case we can access new course after window
                getCc().setActualPosition(index);
                writeToLogFileAndOutput("--- Strategy analyzing at actual price of: " + actualPrice + " ---");
                trades = getStrategy().processNewCourse(trades, getCc());
            }
            tm.processTrades(trades, getCc().getBidPrice(index), getCc().getClose(index), this.actualTime);
            printCurrentStats(index);
        }
        if (writeToLogFile) {
            try {
                bw.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(StrategySimulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new SimulationResults(this, tm);

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
        double ask = getCc().getAskPrice(index);
        String output = String.format("%s: Course:%6f | Active/Closed Trades:%3d/%3d | Balance:%6d | Equity:%6d | Used Margin:%6d | Usable Margin:%6d", sdf.format(new Date(actualTime)), ask, tm.getActiveTradesCount(), tm.getClosedTradesCount(), Math.round(this.tm.getBalance()), Math.round(this.tm.getEquity()), Math.round(this.tm.getUsedMargin()), Math.round(this.tm.getUsableMargin()));
        writeToLogFileAndOutput(output);
    }

    public static void writeToLogFileAndOutput(String text) {
        System.out.println(text);
        if (writeToLogFile) {
            try {
                bw.write(text);
                bw.newLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @return the strategy
     */
    public AbstractStrategy getStrategy() {
        return strategy;
    }

    /**
     * @return the cc
     */
    public CurrencyCourseOHLC getCc() {
        return cc;
    }

}

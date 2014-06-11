/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import datacollection.CurrencyCourseCreator;
import datacollection.CurrencyCourseOHLC;
import datacollection.FormatHistDataOHLCWithSpread;
import forexstrategies.AbstractStrategy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javamt4interface.InputOrganizer;
import javamt4interface.JavaMT4Interface;

/**
 *
 * @author Moohdyy
 */
public class StrategyProcesser extends Thread {

    private CurrencyCourseOHLC cc;
    private CurrencyCourseCreator ccc;
    private InputOrganizer io;
    private List<Trade> actualTrades;
    private AbstractStrategy strategy;
    private int actualFileSize = 0;
    private int activeTradesCount;
    private boolean active = true;

    public StrategyProcesser(InputOrganizer io, AbstractStrategy strategy) {
        ccc = new FormatHistDataOHLCWithSpread();
        this.io = io;
        this.strategy = strategy;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            while (active) {
                int fileSize = 0;
                try {
                    fileSize = CurrencyCourseCreator.count(io.getOhlcFile().getCanonicalPath());
                    JavaMT4Interface.printToOutput("OHLC data detected: " + fileSize);
                } catch (IOException e) {
                    JavaMT4Interface.printToOutput("Error while parsing file: " + io.getOhlcFile().getName() + " : " + e.getMessage());
                }
                if (actualFileSize != fileSize) {
                    try {
                        cc = ccc.getCurrencyCourseFromFile(io.getOhlcFile().getCanonicalPath(), "");
                        actualTrades = getTradesFromFile(io.getTradesFile());
                    } catch (IOException | ParseException ex) {
                        JavaMT4Interface.printToOutput("Error while parsing files: " + ex.getMessage());
                    }
                    cc.setActualPosition(cc.getNumberOfEntries() - 1);
                    strategy.setCurrencyCourseOHLC(cc);
                    List<Trade> newTrades = strategy.processNewCourse(actualTrades, cc);
                    try {
                        writeTradesToFile(io.getTradesFile(), newTrades);
                    } catch (IOException ex) {
                        JavaMT4Interface.printToOutput("Error while writing trades: " + io.getTradesFile().getName() + ex.getMessage());
                    } catch (ParseException ex) {
                        JavaMT4Interface.printToOutput("Error while writing trades: " + io.getTradesFile().getName() + ex.getMessage());
                    }
                    actualFileSize = fileSize;
                } else {


                    // UI updaten
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // entsprechende UI Komponente updaten
                        }
                    });

                    // Thread schlafen
                    try {
                        // fuer 3 Sekunden
                        sleep(TimeUnit.SECONDS.toMillis(3));
                        JavaMT4Interface.printToOutput("Waiting for changes.");
                    } catch (InterruptedException ex) {
                        JavaMT4Interface.printToOutput("Error while trying to sleep thread.");
                        System.out.println("ERROR while sleeping: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private List<Trade> getTradesFromFile(File tradesFile) throws IOException, ParseException {
        JavaMT4Interface.printToOutput("Reading trades.");
        FileReader fr = new FileReader(tradesFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String[] oneLine;
        // date format: 2012-01-02 02:00:02.183000000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        long timeStampOpen, timeStampClose;
        boolean active;
        int mt4ID, orderType;
        double open, close, lotSize, tp, sl;
        List<Trade> tradeList = new ArrayList<>();
        //from mt4: output=active+D+mt4ID+D+orderType+D+openTime+D+closeTime+D+openPrice+D+closePrice+D+lotSize+D+takeProfit+D+stopLoss;
        // true;1954704;1;2014.06.10 11:05:42;1970.01.01 00:00:00;1.35868;1.35883;1;0;0
        int count = 0;
        while ((line = br.readLine()) != null) {
            oneLine = line.split(";");
            active = Boolean.parseBoolean(oneLine[0]);
            mt4ID = Integer.parseInt(oneLine[1]);
            orderType = Integer.parseInt(oneLine[2]);
            timeStampOpen = sdf.parse(oneLine[3]).getTime();
            timeStampClose = sdf.parse(oneLine[4]).getTime();
            open = Double.parseDouble(oneLine[5]);
            close = Double.parseDouble(oneLine[6]);
            lotSize = Double.parseDouble(oneLine[7]);
            tp = Double.parseDouble(oneLine[8]);
            sl = Double.parseDouble(oneLine[9]);
            Trade trade = new Trade(orderType, lotSize);
            trade.setMT4ID(mt4ID);
            trade.setOpeningPrice(open);
            trade.setTimeStampOpen(timeStampOpen);
            trade.setTimeStampClose(timeStampClose);
            trade.setTakeProfit(tp);
            trade.setStopLoss(sl);
            if (!active) {
                trade.close();
            }
            tradeList.add(trade);
            count++;
        }
        br.close();
        fr.close();
        JavaMT4Interface.printToOutput(count + " existing Trades found.");
        activeTradesCount = count;
        return tradeList;
    }

    private void writeTradesToFile(File tradesFile, List<Trade> newTrades) throws FileNotFoundException, IOException, ParseException {
        if (activeTradesCount == newTrades.size()) {
            JavaMT4Interface.printToOutput("No new Trades from strategy.");
            return;
        }
        if (!tradesFile.delete()) {
            JavaMT4Interface.printToOutput("Error deleting old tradeFile.");
        }
        JavaMT4Interface.printToOutput("Writing " + (newTrades.size() - activeTradesCount) + " new Trades.");
        FileWriter fw = new FileWriter(tradesFile);
        BufferedWriter bw = new BufferedWriter(fw);
        // date format: 2012-01-02 02:00:02.183000000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        //from mt4: output=active+D+mt4ID+D+orderType+D+openTime+D+closeTime+D+openPrice+D+closePrice+D+lotSize+D+takeProfit+D+stopLoss;
        // true;1954704;1;2014.06.10 11:05:42;1970.01.01 00:00:00;1.35868;1.35883;1;0;0
        String D = ";";
        int mt4ID = 0;
        double closingPrice = 0.0;
        for (Trade trade : newTrades) {
            String tradeString = trade.isOpen() + D + mt4ID + D + trade.getTradeType() + D + sdf.format(new Date(trade.getTimeStampOpen())) + D + "" + D + trade.getOpeningPrice() + D + closingPrice + D + trade.getVolume() + D + trade.getTakeProfit() + D + trade.getStopLoss() + "\n";
            JavaMT4Interface.printToOutput("new Trade: " + tradeString);
            bw.write(tradeString);
        }
        bw.close();
        fw.close();
    }

    public void stopThread() {
        active = false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import datacollection.CurrencyCourseCreator;
import datacollection.CurrencyCourseOHLC;
import datacollection.FormatHistDataOHLCWithSpread;
import datacollection.OHLC;
import forexstrategies.AbstractStrategy;
import forexstrategies.JapaneseCandlesticksStrategy;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javamt4interface.InputOrganizer;
import javamt4interface.JavaMT4Interface;

/**
 *
 * @author Moohdyy
 */
public class StrategyProcesser {

    private CurrencyCourseOHLC cc;
    private int actualFileSize = 0;
    private boolean active = true;

    public StrategyProcesser(InputOrganizer io, AbstractStrategy strategy) {
        CurrencyCourseOHLC cc = null;
        List<Trade> actualTrades = null;
        CurrencyCourseCreator ccc = new FormatHistDataOHLCWithSpread();
        while (active) {
            int fileSize = 0;
            try {
                fileSize = CurrencyCourseCreator.count(io.getOhlcFile().getCanonicalPath());
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
                cc.setActualPosition(cc.getNumberOfEntries());
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
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    JavaMT4Interface.printToOutput("Error while trying to sleep thread.");
                }
            }
        }
    }

    private List<Trade> getTradesFromFile(File tradesFile) throws IOException, ParseException {
        FileInputStream fis = new FileInputStream(tradesFile);
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
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

        }
        return tradeList;
    }

    private void writeTradesToFile(File tradesFile, List<Trade> newTrades) throws FileNotFoundException, IOException, ParseException {
        FileOutputStream fos = new FileOutputStream(tradesFile);
        DataOutputStream out = new DataOutputStream(fos);
        BufferedWriter br = new BufferedWriter();
        wnbi+´n493ng
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

        }

    }

}

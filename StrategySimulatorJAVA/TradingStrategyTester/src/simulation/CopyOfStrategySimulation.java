/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import datacollection.CurrencyCourseOHLC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import strategies.AbstractStrategy;

/**
 *
 * @author Moohdyy
 */
public class CopyOfStrategySimulation {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private CopyOfTradeManager tm;
    private AbstractStrategy strategy;
    private CurrencyCourseOHLC cc;
    private long actualTime = 0;

    public CopyOfStrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance) {
        this.cc = cc;
        this.strategy = strategy;
        this.tm = new CopyOfTradeManager(balance);
    }

    public CopyOfStrategySimulation(AbstractStrategy strategy, CurrencyCourseOHLC cc, double balance, double leverage) {
        this(strategy, cc, balance);
        this.tm.setLeverage(leverage);
    }

    //for this simulation, the strategy just has one order at maximum open and can close a BUY order by returning SELL and vice versa
    public void simulateStrategy(int windowInMinutes) {
        long windowInMilliseconds = windowInMinutes * 60 * 1000;
        List<Trade> trades = new ArrayList<>();
        for (int index = 0; index < cc.getNumberOfEntries(); index++) {
            double actualPrice = cc.getClose(index);
            if (tm.checkForMarginCall()) {
               // System.out.println("MARGIN CALL, simulation stopped.");
                return;
            }
            tm.checkStopLossTakeProfit(actualPrice);
            this.actualTime = cc.getTimeStamp(index);
            long windowTime = cc.getTimeStamp(cc.getActualPosition());
            if (this.actualTime >= windowTime + windowInMilliseconds) { //in our case we can access new course after window
                cc.setActualPosition(index);
               // System.out.println("Actual price of " + cc.getCurrencyPair() + " : " + actualPrice);
                trades = strategy.processNewCourse(trades, cc);
            }
            tm.processTrades(trades, cc.getBidPrice(index), cc.getClose(index), this.actualTime);
            printCurrentStats(index);
        }
    }

    public void printCurrentStats(int index) {
//        double bid = cc.getBidPrice(index);
//        double ask = cc.getAskPrice(index);
//        String output="________________________________"+System.lineSeparator() + sdf.format(new Date(actualTime));
////                + ":"+System.lineSeparator()+" ActualPrice:   " + bid
////                +System.lineSeparator()+" ActiveTrades:  " + tm.getActiveTradesCount()
////                +System.lineSeparator()+ ", ClosedTrades: " + tm.getClosedTradesCount()
////                	+System.lineSeparator()+" Balance:       " + Math.round(this.tm.getBalance())
////                +System.lineSeparator()+" Equity:        " + Math.round(this.tm.getEquity())
////               +System.lineSeparator()+" UseableMargin: " + "Not Implemented"
////               +System.lineSeparator()+" Margin:        " + Math.round(this.tm.getFreeMargin())
////                +System.lineSeparator()+"________________________________________"+System.lineSeparator()+"";
////        
//        try {
//        
//			FileWriter fw=new FileWriter(new File("output.txt"),true);
//			BufferedWriter bw=new BufferedWriter(fw);
//			bw.write(output+System.lineSeparator());
//			bw.close();
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.print(output);
    }

}

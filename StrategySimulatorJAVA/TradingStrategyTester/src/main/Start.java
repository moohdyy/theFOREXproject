package main;

import datacollection.CurrencyCourseCreator;
import datacollection.CurrencyCourseOHLC;
import datacollection.FormatHistDataOHLCWithoutSpread;
import datacollection.FormatMT4OHLC;
import indicators.SMA;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import simulation.CopyOfStrategySimulation;
import simulation.StrategySimulation;
import strategies.AbstractStrategy;
import strategies.JapaneseCandlestick;
import strategies.JapaneseCandlesticksStrategy;
import strategies.SimpleTestStrategySMA;

public class Start {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        // getting actual course
        CurrencyCourseCreator ccc = new FormatHistDataOHLCWithoutSpread();
        CurrencyCourseOHLC cc = new CurrencyCourseOHLC();
        try {
            cc = ccc.getCurrencyCourseFromFile("historicalData\\EURUSD\\2011\\oneDayTest.csv", "EURUSD");
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        //determine spread depending on some pipchange
        int pipsForSpread = 2;
        double onePip = cc.getOHLC(0).getClose()/10000;
        double spread = onePip * pipsForSpread;
        cc.setSpread(spread);
        
        //parameters for this strategy
        SMA sma = new SMA();
        int smaDuration = 5;
        sma.calculateSMA(cc, smaDuration);
        
        //initialize the strategy
        JapaneseCandlesticksStrategy simpleTestStrategy = new JapaneseCandlesticksStrategy(cc);
        
        
        // start simulation
        int leverage = 5;
        double balance = 50000;
        int timeframeInMinutes = 2;
        CopyOfStrategySimulation simulation = new CopyOfStrategySimulation(simpleTestStrategy, cc, balance, leverage);
        simulation.simulateStrategy(timeframeInMinutes);
    }
}

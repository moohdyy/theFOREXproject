package main;

import datacollection.CurrencyCourseCreator;
import datacollection.CurrencyCourseOHLC;
import datacollection.FormatMT4OHLC;
import indicators.SMA;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.StrategySimulation;
import strategies.AbstractStrategy;
import strategies.SimpleTestStrategySMA;

public class Start {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        // getting actual course
        CurrencyCourseCreator ccc = new FormatMT4OHLC();
        CurrencyCourseOHLC cc = new CurrencyCourseOHLC();
        try {
            cc = ccc.getCurrencyCourseFromFile("EURUSD.1.csv", "EURUSD");
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //parameters for this strategy
        SMA sma = new SMA();
        int smaDuration = 5;
        sma.calculateSMA(cc, smaDuration);
        
        //initialize the strategy
        AbstractStrategy simpleTestStrategy = new SimpleTestStrategySMA(cc,sma.getSMAStrings());
        
        
        // start simulation
        int leverage = 5;
        double balance = 50000;
        int timeframeInMinutes = 100;
        StrategySimulation simulation = new StrategySimulation(simpleTestStrategy, cc, balance, leverage);
        simulation.simulateStrategy(timeframeInMinutes);
    }
}

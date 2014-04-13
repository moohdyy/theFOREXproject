package main;

import indicators.SMA;
import datacollection.CurrencyCourse;
import datacollection.CurrencyCourseCreator;
import datacollection.FormatGAINCapital;
import de.flohrit.mt4j.AbstractBasicClientMINE;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.StrategySimulation;
import strategies.SimpleTestStrategySMA;

public class Start {

    /**
     * @param args
     */
    public static void main(String[] args) {
        CurrencyCourseCreator ccc = new FormatGAINCapital();
        CurrencyCourse cc = new CurrencyCourse();
        try {
            cc = ccc.getCurrencyCourseFromFile("EUR_USD_Week1_2012-01-02_GAINCapital.csv");
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        SMA sma = new SMA();
        sma.calculateSMA(cc, 5);
        AbstractBasicClientMINE strategy = new SimpleTestStrategySMA(sma.getSMAStrings(), cc);
        StrategySimulation simulation = new StrategySimulation(strategy, cc, 50000, 500);
        simulation.simulateOneOrderStrategy();
    }
}

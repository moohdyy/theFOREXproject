package main;

import datacollection.CurrencyCourseCreator;
import datacollection.CurrencyCourseOHLC;
import datacollection.FormatHistDataOHLCWithoutSpread;
import datacollection.FormatMT4OHLC;
import indicators.SMA;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.StrategySimulation;
import strategies.AbstractStrategy;
import strategies.SimpleTestStrategySMA;

public class Start {

    public static String FOLDERNAME = "SimulationResults\\";

    /**
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        // getting actual course
        CurrencyCourseCreator ccc = new FormatHistDataOHLCWithoutSpread();
        String filename;

        //just testing with a little file
//        filename = "historicalData\\EURUSD\\2011\\oneDayTest.csv";
//        simulateOneFile(filename, ccc);

        //whole simulation
        File results = new File("results_" + System.currentTimeMillis() + ".txt");
        FileWriter fw = new FileWriter(results, true);
        BufferedWriter bw = new BufferedWriter(fw);
        String[] currencyPairs = {"EURUSD", "GBPUSD", "USDJPY"};
        String[] years = {"2011", "2012"};

        double result = 0;

        for (String currencyPair : currencyPairs) {
            for (String year : years) {
                for (int month = 1; month < 12; month++) {
                    result = simulateOneFile("historicalData\\" + currencyPair + "\\" + year + "\\" + month + ".csv", ccc);
                    bw.write(currencyPair+","+year+","+ month +": final Balance: "+result+ System.lineSeparator());
                }
            }
        }
        bw.close();
        fw.close();
    }

    private static double simulateOneFile(String filename, CurrencyCourseCreator ccc) {
        CurrencyCourseOHLC cc = new CurrencyCourseOHLC();
        try {
            cc = ccc.getCurrencyCourseFromFile(filename, "EURUSD");
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        //determine spread depending on some pipchange
        int pipsForSpread = 2;
        double onePip = cc.getOHLC(0).getClose() / 10000;
        double spread = onePip * pipsForSpread;
        cc.setSpread(spread);

        //initialize the strategy
        AbstractStrategy thisStrategy = new SimpleTestStrategySMA(cc);

        // start simulation
        int leverage = 5;
        double balance = 50000;
        int timeframeInMinutes = 2;
        File f = new File(FOLDERNAME + thisStrategy.getName());
        f.mkdirs();
        StrategySimulation simulation = new StrategySimulation(thisStrategy, cc, balance, leverage);
        return simulation.simulateStrategy(timeframeInMinutes);
    }
}

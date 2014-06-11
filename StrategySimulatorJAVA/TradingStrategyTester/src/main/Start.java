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

import simulation.SimulationResults;
import simulation.StrategySimulation;
import strategies.AbstractStrategy;
import strategies.JapaneseCandlesticksStrategy;
import strategies.SimpleTestStrategySMA;
import strategies.Testing;

public class Start {

    private static String BASICFOLDERNAME = "SimulationResults\\";
    public static String FOLDERNAME;
    /**
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        // getting actual course
        CurrencyCourseCreator ccc = new FormatHistDataOHLCWithoutSpread();
        String filename;

        boolean writeToLogFile = true;

        //just testing with a little file
//        filename = "historicalData\\EURUSD\\2011\\oneDayTest.csv";
//        simulateOneFile(filename, ccc,true);
        //whole simulation
        String[] currencyPairs = {"EURUSD","GBPUSD", "USDJPY"};
        String[] years = {"2011", "2012"};

        SimulationResults result;
        FOLDERNAME = BASICFOLDERNAME + "\\results_" + System.currentTimeMillis() +  "\\" ;
        (new File(FOLDERNAME)).mkdirs();
        filename = FOLDERNAME+ "overviewResults.csv";
        writeLineToFile(filename, SimulationResults.getHeader());

        for (String currencyPair : currencyPairs) {
            for (String year : years) {
                for (int month = 1; month < 13; month++) {
                    result = simulateOneFile("historicalData\\" + currencyPair + "\\" + year + "\\" + month + ".csv",currencyPair, ccc, writeToLogFile);
                	//result = simulateOneFile("C:\\Users\\Carina\\Downloads\\faultyDataSample.csv", ccc, writeToLogFile);
                    writeLineToFile(filename, result.toString());
                }
//                for (int month = 1; month < 13; month++) {
//                    result = simulateOneFile("historicalData\\" + currencyPair + "\\" + year + "\\" + month + ".csv", ccc, writeToLogFile);
//                	//result = simulateOneFile("C:\\Users\\Carina\\Downloads\\faultyDataSample.csv", ccc, writeToLogFile);
//                    
//                    writeLineToFile(filename, result.toString());
//                }
            }
        }
    }

    private static SimulationResults simulateOneFile(String filename,String currencyPair, CurrencyCourseCreator ccc, boolean writeToLogFile) {
        CurrencyCourseOHLC cc = new CurrencyCourseOHLC();
        try {
            cc = ccc.getCurrencyCourseFromFile(filename,currencyPair);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        //determine spread depending on some pipchange
        int pipsForSpread = 5;
        cc.setSpread(pipsForSpread);

        //initialize the strategy
        AbstractStrategy thisStrategy = new JapaneseCandlesticksStrategy(cc);
        //AbstractStrategy thisStrategy=new Testing(cc,"Testing");
        // start simulation
        int leverage = 100;
        double balance = 50000;
        int timeframeInMinutes = 2;
        File f = new File(FOLDERNAME + thisStrategy.getName());
        f.mkdirs();
        StrategySimulation simulation = new StrategySimulation(thisStrategy, cc, balance, leverage, writeToLogFile);
        return simulation.simulateStrategy(timeframeInMinutes);
    }

    private static void writeLineToFile(String filename, String input) {
        FileWriter fw = null;
        try {
            File file = new File(filename);
            fw = new FileWriter(file, true);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(input);
                bw.newLine();
            }
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

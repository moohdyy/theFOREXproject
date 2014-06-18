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
       // filename = "historicalData\\EURUSD\\2011\\oneDayTest.csv";
       //simulateOneFile(filename, "EURUSD", ccc,true);
        //whole simulation
        String[] currencyPairs = {"EURUSD","GBPUSD", "USDJPY"};
        String[] years = {"2014"};
        String[] timeframes = {"60","30","15","5","1"};
        int[]laverages={1,3,5,10,25,50,100};
        double [] pips={0.0,5.0,10.0,20.0,50.0};
        SimulationResults result;
        FOLDERNAME = BASICFOLDERNAME + "\\results_" + System.currentTimeMillis() +  "\\" ;
        (new File(FOLDERNAME)).mkdirs();
        filename = FOLDERNAME+ "overviewResults.csv";
        writeLineToFile(filename, SimulationResults.getHeader());

       // for (String currencyPair : currencyPairs) {
          //  for (String year : years) {
             //   for (int month = 1; month < 7; month++) {
        String currencyPair="EURUSD";
    	String year="2014";
        for(String timeframe:timeframes)
        {
        	for(int i=0;i<pips.length;i++)
        	{
            	for(int j=0;j<pips.length;j++)
            	{
            		//for(int l:laverages)
            		{
            			int l=5;
                    result = simulateOneFile("historicalData\\" + currencyPair + "\\" + year + "\\EURUSD" + timeframe + ".csv",currencyPair, ccc,timeframe,pips[i],pips[j],l, writeToLogFile);
                	//result = simulateOneFile("C:\\Users\\Carina\\Downloads\\faultyDataSample.csv", ccc, writeToLogFile);
                    writeLineToFile(filename, result.toString());
                    System.out.println("Finished M"+timeframe+" "+pips[i]+" "+pips[j]);
            		}
                }
        	}
        }
//                for (int month = 1; month < 13; month++) {
//                    result = simulateOneFile("historicalData\\" + currencyPair + "\\" + year + "\\" + month + ".csv", ccc, writeToLogFile);
//                	//result = simulateOneFile("C:\\Users\\Carina\\Downloads\\faultyDataSample.csv", ccc, writeToLogFile);
//                    
//                    writeLineToFile(filename, result.toString());
//                }
            //}
       // }
    }

    private static SimulationResults simulateOneFile(String filename,String currencyPair, CurrencyCourseCreator ccc,String timeframe,double stoppLoss,double takeProfit,int leverage, boolean writeToLogFile) {
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
        AbstractStrategy thisStrategy = new JapaneseCandlesticksStrategy(new CurrencyCourseOHLC(cc.getCurrencyPair()),stoppLoss,takeProfit);
       // AbstractStrategy thisStrategy=new Testing(cc,"Testing");
        // start simulation
        double balance = 50000;
        int timeframeInMinutes = 1;
        File f = new File(FOLDERNAME + thisStrategy.getName());
        f.mkdirs();
        StrategySimulation simulation = new StrategySimulation(thisStrategy, cc, balance, timeframe,stoppLoss,takeProfit,leverage, writeToLogFile);
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

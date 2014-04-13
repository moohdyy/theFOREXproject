/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indicators;

import datacollection.CurrencyCourse;
import java.util.HashMap;
import java.util.Objects;

// source: https://code.google.com/p/adaptive-trading-system/source/browse/ATS/src/ats/strategies/Strategies.java?r=11
// Simple moving average
public class SMA {

    HashMap<Long, Double> SMAvalues = new HashMap<>();
    HashMap<Long, String> SMAstrings = new HashMap<>();
    public static final String rising = "rising";
    public static final String falling = "falling";
    public static final String flat = "flat";

    public SMA() {
    }

    // Get simple moving average
    public void calculateSMA(CurrencyCourse cc, int SMADuration) {

        // Create simple moving average "SMA" attribute
        if (cc.getNumberOfEntries() >= SMADuration) {

            double simpleMovingAverage = 0;
            double sum = 0;
            double tempSum = 0;
            double average = 0;

            // Calculate simple "non-moving" average for
            // the first "SMADuration - 1" instances
            for (int i = 0; i < SMADuration - 1; i++) {
                for (int j = 0; j <= i; j++) {
                    tempSum += cc.getBidPrice(j);
                    average = tempSum / (j + 1);
                }
                tempSum = 0;

                //actual time
                long actTime = cc.getTimeStamp(i);
                // Record simple "non-moving" average
                SMAvalues.put(actTime, average);

                // Insert simple "non-moving" average into data set
                if (i == 0) {
                    SMAstrings.put(actTime, flat);
                } else {
                    long lastTime = cc.getTimeStamp(i - 1);
                    if (SMAvalues.get(actTime) > SMAvalues.get(lastTime)) {
                        SMAstrings.put(actTime, rising);
                    } else if (SMAvalues.get(actTime) < SMAvalues.get(lastTime)) {
                        SMAstrings.put(actTime, falling);
                    } else if (Objects.equals(SMAvalues.get(actTime), SMAvalues.get(lastTime))) {
                        SMAstrings.put(actTime, flat);
                    }
                }
            }

            // Calculate SMA
            for (int i = SMADuration - 1; i < cc.getNumberOfEntries(); i++) {
                for (int j = 0; j < SMADuration; j++) {
                    sum += cc.getBidPrice(i - j);
                }
                simpleMovingAverage = sum / SMADuration;
                sum = 0;

                //timestamps
                long actTime = cc.getTimeStamp(i);
                long lastTime = cc.getTimeStamp(i - 1);
                // Record SMA
                SMAvalues.put(actTime, simpleMovingAverage);

                // Insert SMA into data set
                if (SMAvalues.get(actTime) > SMAvalues.get(lastTime)) {
                    SMAstrings.put(actTime, rising);
                } else if (SMAvalues.get(actTime) < SMAvalues.get(lastTime)) {
                    SMAstrings.put(actTime, falling);
                } else if (Objects.equals(SMAvalues.get(actTime), SMAvalues.get(lastTime))) {
                    SMAstrings.put(actTime, flat);
                }

            }
        }
    }

    // Get numeric SMA values 
    public HashMap<Long, Double> getSMAValues() {
        return SMAvalues;
    }

    // Get numeric SMA values 
    public HashMap<Long, String> getSMAStrings() {
        return SMAstrings;
    }

}

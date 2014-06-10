package indicators;

import datacollection.CurrencyCourseOHLC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

// source: https://code.google.com/p/adaptive-trading-system/source/browse/ATS/src/ats/strategies/Strategies.java?r=11
// Exponential moving average
public class EMA {

    HashMap<Long, Double> EMAvalues = new HashMap<>();
    HashMap<Long, String> EMAstrings = new HashMap<>();
    public static final String rising = "rising";
    public static final String falling = "falling";
    public static final String flat = "flat";

    ArrayList<Double> EMASet = new ArrayList<Double>();

    public EMA() {
    }

    // Get exponential moving average
    public void getEMA (CurrencyCourseOHLC cc, int EMADuration) {

        if (cc.getNumberOfEntries() >= EMADuration) {

            double simpleMovingAverage = 0;
            double exponentialMovingAverage = 0;
            double sum = 0;
            double multiplier = (double) (2 / ((double) EMADuration + 1)); // Weighting
            // multiplier
            double tempSum = 0;
            double average = 0;

                        // Calculate simple "non-moving" average for
            // the first "EMADuration - 1" instances
            for (int i = 0; i < EMADuration - 1; i++) {
                for (int j = 0; j <= i; j++) {
                    tempSum += cc.getClose(j);
                    average = tempSum / (j + 1);
                }
                tempSum = 0;

                //actual time
                long actTime = cc.getTimeStamp(i);
                // Record simple "non-moving" average
                EMAvalues.put(actTime, average);

                // Insert simple "non-moving" average into data set
                if (i == 0) {
                     EMAstrings.put(actTime, flat);
                } else {
                    long lastTime = cc.getTimeStamp(i - 1);
                    if (EMAvalues.get(actTime) > EMAvalues.get(lastTime)) {
                     EMAstrings.put(actTime, rising);
                    } else if (EMAvalues.get(actTime) < EMAvalues.get(lastTime)) {
                     EMAstrings.put(actTime, falling);
                    } else if (Objects.equals(EMAvalues.get(actTime), EMAvalues.get(lastTime))) {
                     EMAstrings.put(actTime, flat);
                    }
                }
            }

            // Simple moving average of the first "EMADuration" instances
            for (int i = 0; i < EMADuration; i++) {
                    sum += cc.getClose(i);
            }
            simpleMovingAverage = sum / EMADuration;
            sum = 0;
            // Record simple moving average of the first "EMADuration" instances
            
                long tmpTime = cc.getTimeStamp(EMADuration - 1);
                EMAvalues.put(tmpTime, simpleMovingAverage);
            simpleMovingAverage = 0;

            // Calculate EMA
            for (int i = EMADuration; i < cc.getNumberOfEntries(); i++) {
                  long actTime = cc.getTimeStamp(i);
                  long lastTime = cc.getTimeStamp(i - 1);
                  
                exponentialMovingAverage = (cc.getClose(i) - EMAvalues.get(lastTime))* multiplier+ EMAvalues.get(lastTime);
                // Record EMA
                EMAvalues.put(actTime, exponentialMovingAverage);
            }

            for (int i = EMADuration - 1; i < cc.getNumberOfEntries(); i++) {
                  long actTime = cc.getTimeStamp(i);
                  long lastTime = cc.getTimeStamp(i - 1);
                 if (EMAvalues.get(actTime) > EMAvalues.get(lastTime)) {
                     EMAstrings.put(actTime, rising);
                    } else if (EMAvalues.get(actTime) < EMAvalues.get(lastTime)) {
                     EMAstrings.put(actTime, falling);
                    } else if (Objects.equals(EMAvalues.get(actTime), EMAvalues.get(lastTime))) {
                     EMAstrings.put(actTime, flat);
                    }
                }
            }
        }
    

    // Get numeric EMA values 
    public HashMap<Long, Double> getEMAValues() {
        return EMAvalues;
    }

    // Get numeric EMA values 
    public HashMap<Long, String> getEMAStrings() {
        return EMAstrings;
    }

}

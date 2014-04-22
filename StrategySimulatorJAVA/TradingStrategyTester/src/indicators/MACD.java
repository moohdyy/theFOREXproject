package indicators;

import datacollection.CurrencyCourseOHLC;
import java.util.ArrayList;
import java.util.HashMap;

// source: https://code.google.com/p/adaptive-trading-system/source/browse/ATS/src/ats/strategies/Strategies.java?r=11
// Moving Average Convergence-Divergence
public class MACD {

    HashMap<Long, Double> MACDvalues = new HashMap<>();
    HashMap<Long, String> MACDstrings = new HashMap<>();
    ArrayList<Double> MACDSet = new ArrayList<Double>();
    public static final String positive = "positive";
    public static final String negative = "negative";
    public static final String centerline = "centerline";

    public MACD() {
    }

    public void getMACD(CurrencyCourseOHLC cc, EMA ema) {

        // Get 12-Day EMA
        EMA EMA_12Day = new EMA();
        EMA_12Day.getEMA(cc, 12);
        HashMap<Long, Double> EMA_12DaySet = EMA_12Day.getEMAValues();

        // Get 26_Day EMA
        EMA EMA_26Day = new EMA();
        EMA_26Day.getEMA(cc, 26);
        HashMap<Long, Double> EMA_26DaySet = EMA_26Day.getEMAValues();

        // Calculate MACD
        for (Long timestamp : EMA_12DaySet.keySet()) {
            MACDvalues.put(timestamp, EMA_12DaySet.get(timestamp) - EMA_26DaySet.get(timestamp));
        }

        // Set "MACD" attribute
        for (int i = 0; i < cc.getNumberOfEntries(); i++) {
            long actTime = cc.getTimeStamp(i);
            if (MACDvalues.get(actTime) > 0) {
                MACDstrings.put(actTime, positive);
            } else if (MACDvalues.get(actTime) < 0) {
                MACDstrings.put(actTime, negative);
            } else if (MACDvalues.get(actTime) == 0) {
                MACDstrings.put(actTime, centerline);
            }
        }

    }
}

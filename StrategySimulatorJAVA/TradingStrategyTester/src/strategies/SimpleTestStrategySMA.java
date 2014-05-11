/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strategies;

import datacollection.CurrencyCourseOHLC;
import datacollection.OHLC;
import indicators.SMA;
import java.util.HashMap;
import java.util.List;
import simulation.Trade;

/**
 *
 * @author Moohdyy
 */
public class SimpleTestStrategySMA extends AbstractStrategy {

    private HashMap<Long, String> SMAstrings;
    private int actualTradeIndex;

    
    public SimpleTestStrategySMA(CurrencyCourseOHLC currencyCourseOHLC) {
        
        super(currencyCourseOHLC,"SimpleTestStrategySMA");//parameters for this strategy
        SMA sma = new SMA();
        int smaDuration = 5;
        sma.calculateSMA(currencyCourseOHLC, smaDuration);
        this.SMAstrings = sma.getSMAStrings();
    }
    

    @Override
    public List<Trade> processNewCourse(List<Trade> actualTrades, CurrencyCourseOHLC currencyCourse) {
        actualTradeIndex = actualTrades.size() - 1;
        OHLC actualOHLC = getActualOHLC();
        int action = Trade.NOACTION;
        switch (SMAstrings.get(actualOHLC.getTimestamp())) {
            case "rising":
                action = Trade.BUY;
                break;
            case "falling":
                action = Trade.SELL;
                break;
        }
        if (action != Trade.NOACTION) {
            buyOrSell(action, actualTrades);
        }
        return actualTrades;
    }

    private void buyOrSell(int action, List<Trade> actualTrades) {
        if (actualTradeIndex == -1 || !actualTrades.get(actualTradeIndex).isOpen()) {
            actualTrades.add(new Trade(action, 100000));
        } else {
            Trade activeTrade = actualTrades.get(actualTradeIndex);
            if (action != activeTrade.getTradeType()) { //if contrary sign, close trade
                activeTrade.close();
            }
        }
    }

}

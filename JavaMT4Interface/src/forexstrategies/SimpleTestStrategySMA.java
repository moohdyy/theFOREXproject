package forexstrategies;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private static double RELATIVEDISTANCE_FOR_TP_SL = 0.001;

    public SimpleTestStrategySMA(CurrencyCourseOHLC currencyCourseOHLC) {

        super(currencyCourseOHLC, "SimpleTestStrategySMA");//parameters for this strategy
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
            buyOrSell(action, actualTrades, actualOHLC.getClose());
        }
        return actualTrades;
    }

    private void buyOrSell(int action, List<Trade> actualTrades, double actualPrice) {
        if (actualTradeIndex == -1 || !actualTrades.get(actualTradeIndex).isOpen()) {
            Trade trade = new Trade(action, 1000000);
            double takeprofit = -1;
            double stoploss = -1;
            switch (action) {
                case Trade.BUY:
                    takeprofit = actualPrice * (1+RELATIVEDISTANCE_FOR_TP_SL);
                    stoploss = actualPrice * (1-RELATIVEDISTANCE_FOR_TP_SL);
                    break;
                case Trade.SELL:
                    takeprofit = actualPrice * (1-RELATIVEDISTANCE_FOR_TP_SL);
                    stoploss = actualPrice * (1+RELATIVEDISTANCE_FOR_TP_SL);
                    break;
            }
            trade.setTakeProfit(takeprofit);
            trade.setStopLoss(stoploss);
            actualTrades.add(trade);
        } else {
            Trade activeTrade = actualTrades.get(actualTradeIndex);
            if (action != activeTrade.getTradeType()) { //if contrary sign, close trade
                activeTrade.close();
            }
        }
    }

}

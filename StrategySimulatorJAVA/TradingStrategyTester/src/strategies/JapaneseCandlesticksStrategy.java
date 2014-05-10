package strategies;


import java.util.ArrayList;
import java.util.List;

import Connection.Candlestick;
import Connection.Time;
import simulation.Trade;
import datacollection.CurrencyCourseOHLC;
import datacollection.OHLC;

public class JapaneseCandlesticksStrategy extends AbstractStrategy {
	ArrayList<JapaneseCandlestick> japanese=new ArrayList<JapaneseCandlestick>();

	public JapaneseCandlesticksStrategy(CurrencyCourseOHLC currencyCourseOHLC) {
		super(currencyCourseOHLC);
		int number=currencyCourseOHLC.getNumberOfEntries();
		for(int i=0;i<number;i++)
		{
			OHLC ohlc=currencyCourseOHLC.getOHLC(i);
			japanese.add(new JapaneseCandlestick(new Candlestick(new Time(ohlc.getTimestamp()), ohlc.getOpen(), ohlc.getClose(), ohlc.getLow(), ohlc.getHigh())));
		}
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		int number=currencyCourse.getNumberOfEntries();
		for(int i=0;i<number;i++)
		{
			OHLC ohlc=currencyCourse.getOHLC(i);
			japanese.add(new JapaneseCandlestick(new Candlestick(new Time(ohlc.getTimestamp()), ohlc.getOpen(), ohlc.getClose(), ohlc.getLow(), ohlc.getHigh())));
		}
		JapaneseCandlestick candle=null;
		if(japanese.get(0).getTime().getTime()>japanese.get(candles.size()-1).getTime().getTime())
		{
			candle=japanese.get(0);
		}else
		{
			candle=japanese.get(japanese.size()-1);
		}
		boolean buying=JapaneseCandlestick.buyingSignal(candle.getPattern());
		boolean selling=JapaneseCandlestick.sellingSignal(candle.getPattern());
		if(selling)
		{
			for(int i=0;i<actualTrades.size();i++)
			{
				actualTrades.get(i).close();
			}
		}
		else if (buying)
		{
			double t=(candle.getHighestValue()-candle.getLowestValue())/60*currencyCourse.getBidPrice(currencyCourse.getNumberOfEntries()-1)*100000;
			Trade trade=new Trade(Trade.BUY,t);
			actualTrades.add(trade);
		}
		return actualTrades;
	}
	private ArrayList<Candlestick> candles;
}

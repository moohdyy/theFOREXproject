package strategies;

import indicators.SMA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import Connection.Candlestick;
import Connection.Time;
import simulation.Trade;
import strategies.JapaneseCandlestick;
import strategies.JapaneseCandlestick.Patterns;
import datacollection.CurrencyCourseOHLC;
import datacollection.OHLC;

public class JapaneseCandlesticksStrategy extends AbstractStrategy {
	ArrayList<JapaneseCandlestick> japanese = new ArrayList<JapaneseCandlestick>();
	enum Trend {flat, falling, rising};

	public JapaneseCandlesticksStrategy(CurrencyCourseOHLC currencyCourseOHLC) {
		super(currencyCourseOHLC, "JapaneseCandlesticksStrategy");
		int number = currencyCourseOHLC.getNumberOfEntries();
		for (int i = 0; i < number; i++) {
			OHLC ohlc = currencyCourseOHLC.getOHLC(i);
			japanese.add(new JapaneseCandlestick(new Candlestick(new Time(ohlc
					.getTimestamp()), ohlc.getOpen(), ohlc.getClose(), ohlc
					.getLow(), ohlc.getHigh())));
		}
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		int number = currencyCourse.getNumberOfEntries();
		japanese = new ArrayList<>();
		CurrencyCourseOHLC actualCurrencyCourse = new CurrencyCourseOHLC();
		for (int i = 0; i < number; i++) {
			if (i <= currencyCourse.getActualPosition()) {
				actualCurrencyCourse.addOHLC(currencyCourse.getOHLC(i));
			}
			OHLC ohlc = currencyCourse.getOHLC(i);
			japanese.add(new JapaneseCandlestick(new Candlestick(new Time(ohlc
					.getTimestamp()), ohlc.getOpen(), ohlc.getClose(), ohlc
					.getLow(), ohlc.getHigh())));
		}
	
		Trend t=determineTrend(actualCurrencyCourse);
		Patterns pattern = JapaneseCandlestick.determinePattern(japanese,
				currencyCourse.getActualPosition(),t);
		
		JapaneseCandlestick candle = japanese.get(currencyCourse
				.getActualPosition());
		boolean buying = JapaneseCandlestick.buyingSignal(pattern);
		boolean selling = JapaneseCandlestick.sellingSignal(pattern);
		if (pattern != Patterns.None) {
			System.out.println(buying);
			System.out.println(selling);
		}
		if (selling) {
			for (int i = 0; i < actualTrades.size(); i++) {
				actualTrades.get(i).close();
			}
		} else if (buying) {
			double tradeV = (candle.getHighestValue() - candle.getLowestValue())
					/ 60
					* currencyCourse.getBidPrice(currencyCourse
							.getNumberOfEntries() - 1) * 100000;
			System.out.println(tradeV);
			tradeV = 10000;
			Trade trade = new Trade(Trade.BUY, tradeV);
			actualTrades.add(trade);
		}
		return actualTrades;
	}
	public Trend determineTrend(CurrencyCourseOHLC cc)
	{
		SMA sma = new SMA();
		sma.calculateSMA(cc, 7);
		HashMap<Long, Double> sevenSMA = sma.getSMAValues();

		sma = new SMA();
		sma.calculateSMA(cc, 20);
		HashMap<Long, Double> twentySMA = sma.getSMAValues();

		sma = new SMA();
		sma.calculateSMA(cc, 65);
		
		HashMap<Long, Double> sixtyFiveSMA = sma.getSMAValues();
		Set<Long> timestamps=sixtyFiveSMA.keySet();
		Long l=(long) 0;
		for(Long i:timestamps)
		{
			if(i>l)
			{
				l=i;
			}
		}
		if(l!=0)
		{
		double sma65=sixtyFiveSMA.get(l);
		timestamps=twentySMA.keySet();
		l=(long) 0;
		for(Long i:timestamps)
		{
			if(i>l)
			{
				l=i;
			}
		}
		double sma20=twentySMA.get(l);
		timestamps=sevenSMA.keySet();
		l=(long) 0;
		for(Long i:timestamps)
		{
			if(i>l)
			{
				l=i;
			}
		}
		double sma7=sevenSMA.get(l);
		if(sma65>sma20)
		{
			if(sma20>sma7)
			{
				return Trend.falling;
			}
		}
		if(sma7>sma20)
		{
			if(sma20>sma65)
			{
				return Trend.rising;
			}
		}
		}
		return Trend.flat;
	}
}

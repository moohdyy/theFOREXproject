package strategies;

import indicators.CopyOfSMA;
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
	public ArrayList<JapaneseCandlestick> japanese = new ArrayList<JapaneseCandlestick>();
	private CopyOfSMA sma7;
	private CopyOfSMA sma20;
	private CopyOfSMA sma65;

	enum Trend {
		flat, falling, rising
	};

	CurrencyCourseOHLC actualCurrencyCourse = new CurrencyCourseOHLC();

	public JapaneseCandlesticksStrategy(CurrencyCourseOHLC currencyCourseOHLC) {
		super(currencyCourseOHLC, "JapaneseCandlesticksStrategy");
		int number = currencyCourseOHLC.getNumberOfEntries();
		for (int i = 0; i < number; i++) {
			OHLC ohlc = currencyCourseOHLC.getOHLC(i);
			japanese.add(new JapaneseCandlestick(new Candlestick(new Time(ohlc
					.getTimestamp()), ohlc.getOpen(), ohlc.getClose(), ohlc
					.getLow(), ohlc.getHigh())));
		}
		actualCurrencyCourse = new CurrencyCourseOHLC();
		for (int i = 0; i < number; i++) {
			if (i <= currencyCourseOHLC.getActualPosition()) {
				actualCurrencyCourse.addOHLC(currencyCourseOHLC.getOHLC(i));
			} else {
				break;
			}
		}
	}

	private double pipsRiskPerTrade = 1000;
	private double balance = 50000;
	private double laverage = 5;

	public void setLaverage(double l) {
		laverage = l;
	}

	public void setBalance(double b) {
		balance = b;
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		if (sma7 == null) {
			sma7 = new CopyOfSMA(currencyCourse, 7);
		}
		if (sma20 == null) {
			sma20 = new CopyOfSMA(currencyCourse, 20);
		}
		if (sma65 == null) {
			sma65 = new CopyOfSMA(currencyCourse, 65);
		}
		int number = currencyCourse.getNumberOfEntries();
		int start = actualCurrencyCourse.getNumberOfEntries();
		for (int i = start; i < number; i++) {
			if (i <= currencyCourse.getActualPosition()) {
				actualCurrencyCourse.addOHLC(currencyCourse.getOHLC(i));
				OHLC ohlc = actualCurrencyCourse.getOHLC(i);
				japanese.add(new JapaneseCandlestick(new Candlestick(new Time(
						ohlc.getTimestamp()), ohlc.getOpen(), ohlc.getClose(),
						ohlc.getLow(), ohlc.getHigh())));

			} else {
				break;
			}

		}
		//actualCurrencyCourse = AbstractStrategy
		//		.filterOutliers(actualCurrencyCourse);
		int actualPos = cc.getActualPosition();
		Trend t = determineTrend(cc);
		Trend t2 = Trend.flat;
		if(actualPos-2>0)
		{
		cc.setActualPosition(actualPos - 2);
		t2= determineTrend(cc);
		}
		cc.setActualPosition(actualPos);
		Patterns pattern = JapaneseCandlestick.determinePattern(japanese,
				currencyCourse.getActualPosition(), t, t2);

		boolean buying = JapaneseCandlestick.buyingSignal(pattern);
		boolean selling = JapaneseCandlestick.sellingSignal(pattern);
		if (selling) {
			for (int i = 0; i < actualTrades.size(); i++) {
				actualTrades.get(i).close();
			}
		} else if (buying) {
			// double tradeV = (candle.getHighestValue() -
			// candle.getLowestValue())
			// / 60
			// * currencyCourse.getBidPrice(actualCurrencyCourse
			// .getNumberOfEntries() - 1) * 100000;
			// System.out.println(tradeV);
			// tradeV = 10000;
			// Trade trade = new Trade(Trade.BUY, tradeV);
			// actualTrades.add(trade);
	        double tradeV = balance * laverage;
	        tradeV = tradeV / pipsRiskPerTrade;
	       // double stopLoss = tradeV;
	        System.out.println("TradeVolume: "+tradeV);
	        tradeV*=100;
			Trade trade = new Trade(Trade.BUY, tradeV);
			actualTrades.add(trade);
		}
		return actualTrades;
	}

	public Trend determineTrend(CurrencyCourseOHLC cc) {
		double sma65 = this.sma65.calculateSMA(cc);
		double sma20 = this.sma20.calculateSMA(cc);
		double sma7 = this.sma7.calculateSMA(cc);
		if (sma65 > sma20) {
			if (sma20 > sma7) {
				return Trend.falling;
			}
		}
		if (sma7 > sma20) {
			if (sma20 > sma65) {
				return Trend.rising;
			}
		}
		return Trend.flat;
	}
}

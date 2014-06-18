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
/*
 * This class is used to simulate the Japanese Candlesticks Strategy.
 * To read more about this topic, please concern http://www.babypips.com/school/elementary/japanese-candle-sticks.
 */
public class JapaneseCandlesticksStrategy extends AbstractStrategy {
	public ArrayList<JapaneseCandlestick> japanese = new ArrayList<JapaneseCandlestick>();
	private CopyOfSMA sma7;
	private CopyOfSMA sma20;
	private CopyOfSMA sma65;
	private double takeProfitPip = 0.0;
	private double stoppLossPip = 0.0;

	enum Trend {
		flat, falling, rising
	};

	CurrencyCourseOHLC actualCurrencyCourse = new CurrencyCourseOHLC();

	public JapaneseCandlesticksStrategy(CurrencyCourseOHLC currencyCourseOHLC,
			double stoppLoss, double takeProfit) {
		super(currencyCourseOHLC, "JapaneseCandlesticksStrategy");
		this.takeProfitPip = takeProfit;
		this.stoppLossPip = stoppLoss;
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
	/*
	 * Determines if a Japanese Candlestick pattern exists at the given moment.
	 * If it finds a new pattern, it opens a new Trade.
	 * It also closes all other trades, which belonged to an opposite pattern.
	 */
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

		int actualPos = currencyCourse.getActualPosition();
		currencyCourse.setActualPosition(actualPos);
		Trend t = determineTrend(currencyCourse);
		Trend t2 = Trend.flat;
		if (actualPos - 2 > 0) {
			currencyCourse.setActualPosition(actualPos - 2);
			t2 = determineTrend(currencyCourse);
		}
		currencyCourse.setActualPosition(actualPos);
		Patterns pattern = JapaneseCandlestick.determinePattern(japanese,
				currencyCourse.getActualPosition(), t, t2);
		boolean buying = JapaneseCandlestick.buyingSignal(pattern);
		boolean selling = JapaneseCandlestick.sellingSignal(pattern);
		double tradeV = balance * laverage;
		tradeV = tradeV / pipsRiskPerTrade;
		tradeV = 2000;
		OHLC c = currencyCourse.getOHLC(currencyCourse.getActualPosition());
		double k = c.getHigh() - c.getLow();
		k /= 2;
		double pip = c.getHigh() / 10000;
		if (selling) {
			for (int i = 0; i < actualTrades.size(); i++) {
				if (actualTrades.get(i).getTradeType() == Trade.BUY) {
					actualTrades.get(i).close();
				}
			}

			Trade trade = new Trade(Trade.SELL, tradeV);
			trade.setPattern(pattern);
			if (stoppLossPip != 0) {
				trade.setStopLoss(c.getLow() + k + (pip * stoppLossPip));
			}
			if(takeProfitPip!=0)
			{
				trade.setTakeProfit(c.getLow() + k - (pip * takeProfitPip));
			}
			actualTrades.add(trade);
		}
		if (buying) {
			for (int i = 0; i < actualTrades.size(); i++) {
				if (actualTrades.get(i).getTradeType() == Trade.SELL) {
					actualTrades.get(i).close();
				}
			}
			Trade trade = new Trade(Trade.BUY, tradeV);
			trade.setPattern(pattern);
			if (stoppLossPip != 0) {
				trade.setStopLoss(c.getLow() + k - (pip * stoppLossPip));
			}
			if(takeProfitPip!=0)
			{
				trade.setTakeProfit(c.getLow() + k + (pip * takeProfitPip));
			}
			actualTrades.add(trade);
		}
		return actualTrades;
	}
	
	/*
	 * This functions determines, if a trend is happening at the certain moment.
	 * To do this, the simple moving average of 7, 20 and 65 were used.
	 */
	public Trend determineTrend(CurrencyCourseOHLC currencyCourse) {
		double sma65 = this.sma65.calculateSMA(currencyCourse);
		double sma20 = this.sma20.calculateSMA(currencyCourse);
		double sma7 = this.sma7.calculateSMA(currencyCourse);
		if ((sma65 != 0) && (sma20 != 0) && (sma7 != 0)) {
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
		}
		return Trend.flat;
	}

	public static String patternToString(Patterns p) {
		return p.toString();
	}
}

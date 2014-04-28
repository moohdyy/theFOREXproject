package strategies;


import java.util.ArrayList;
import java.util.List;

import Connection.Candlestick;
import simulation.Trade;
import datacollection.CurrencyCourseOHLC;

public class JapaneseCandlesticksStrategy extends AbstractStrategy {

	public JapaneseCandlesticksStrategy(CurrencyCourseOHLC currencyCourseOHLC) {
		super(currencyCourseOHLC);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		// TODO Auto-generated method stub
		return null;
	}
	private ArrayList<Candlestick> candles;
	public boolean buyingSignal()
	{
	return false;
	}
	public boolean sellingSignal()
	{
		return false;
	}
}

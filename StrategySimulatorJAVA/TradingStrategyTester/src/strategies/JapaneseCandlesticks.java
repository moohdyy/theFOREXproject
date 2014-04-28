package strategies;

import java.util.List;

import simulation.Trade;
import datacollection.CurrencyCourseOHLC;

public class JapaneseCandlesticks extends AbstractStrategy {

	public JapaneseCandlesticks(CurrencyCourseOHLC currencyCourseOHLC) {
		super(currencyCourseOHLC);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		// TODO Auto-generated method stub
		return null;
	}

}

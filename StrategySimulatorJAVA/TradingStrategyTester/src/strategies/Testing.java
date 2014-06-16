package strategies;

import java.util.ArrayList;
import java.util.List;

import datacollection.CurrencyCourseOHLC;
import simulation.Trade;

public class Testing extends AbstractStrategy {

	public Testing(CurrencyCourseOHLC currencyCourseOHLC, String name) {
		super(currencyCourseOHLC, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		// TODO Auto-generated method stub

		if (Math.random() < 0.01) {
			for (Trade t : actualTrades) {
				if(t.getTradeType()==Trade.SELL)
				{
				t.close();
				}
			}
			Trade t = new Trade(Trade.SELL, 2000);
			actualTrades.add(t);
		}
		if(Math.random()<0.01)
		{
			for (Trade t : actualTrades) {
			if(t.getTradeType()==Trade.BUY)
			{
				t.close();
			}
			}
			Trade t = new Trade(Trade.BUY, 2000);
			actualTrades.add(t);
		}
		return actualTrades;
	}

}

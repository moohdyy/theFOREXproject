package forexstrategies;


import java.util.List;

import datacollection.CurrencyCourseOHLC;
import simulation.Trade;

public class Testing extends AbstractStrategy {

    public Testing(){
        super("testing");
    }
    
    public Testing(CurrencyCourseOHLC currencyCourseOHLC, String name) {
        super(currencyCourseOHLC, name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<Trade> processNewCourse(List<Trade> actualTrades,
            CurrencyCourseOHLC currencyCourse) {
        // TODO Auto-generated method stub
        for (Trade t : actualTrades) {
            t.close();
        }
        Trade t = new Trade(Trade.SELL, 10000);
        actualTrades.add(t);
        t = new Trade(Trade.BUY, 10000);
        actualTrades.add(t);
        return actualTrades;
    }
}

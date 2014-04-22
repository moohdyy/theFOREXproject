/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategies;

import datacollection.CurrencyCourseOHLC;
import datacollection.OHLC;
import java.util.List;
import simulation.Trade;

/**
 *
 * @author Moohdyy
 */
public abstract class AbstractStrategy {
    public final static int BUY = 1;
    public final static int SELL = 2;
    public final static int NOACTION = 0;
    public CurrencyCourseOHLC cc;
    public double volume;
    
    public AbstractStrategy(CurrencyCourseOHLC currencyCourseOHLC){
        this.cc = currencyCourseOHLC;
    }
    
    public abstract List<Trade> processNewCourse(List<Trade> actualTrades, CurrencyCourseOHLC currencyCourse);
    
    public OHLC getActualOHLC(){
        return this.cc.getOHLCOfActualPosition();
    }
    
}

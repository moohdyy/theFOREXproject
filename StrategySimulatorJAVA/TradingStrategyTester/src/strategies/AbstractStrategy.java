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
    public CurrencyCourseOHLC cc;
    public double volume;
    private final String name;
    
    public AbstractStrategy(CurrencyCourseOHLC currencyCourseOHLC, String name){
        this.cc = currencyCourseOHLC;
        this.name = name;
    }
    
    public abstract List<Trade> processNewCourse(List<Trade> actualTrades, CurrencyCourseOHLC currencyCourse);
    
    public OHLC getActualOHLC(){
        return this.cc.getOHLCOfActualPosition();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
}

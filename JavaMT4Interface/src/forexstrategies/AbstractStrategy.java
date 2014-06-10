/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package forexstrategies;

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
//    private final ParameterFactory paramFactory;

    public AbstractStrategy(String name){
        this.name = name;
    }
    
    public AbstractStrategy(CurrencyCourseOHLC currencyCourseOHLC, String name) {
        this(name);
        this.cc = currencyCourseOHLC;
    }

    public abstract List<Trade> processNewCourse(List<Trade> actualTrades, CurrencyCourseOHLC currencyCourse);

    public OHLC getActualOHLC() {
        return this.cc.getOHLCOfActualPosition();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    public static CurrencyCourseOHLC filterOutliers(CurrencyCourseOHLC filteredOHLCs)
    {
    	int j=0;
    	for(int i=1;i<filteredOHLCs.getNumberOfEntries()-2;i++)
    	{
    		if(((filteredOHLCs.getOHLC(i).getHigh()<filteredOHLCs.getOHLC(i-1).getLow())&&(filteredOHLCs.getOHLC(i).getHigh()<filteredOHLCs.getOHLC(i+1).getLow()))||((filteredOHLCs.getOHLC(i).getLow()>filteredOHLCs.getOHLC(i-1).getHigh())&&(filteredOHLCs.getOHLC(i).getLow()>filteredOHLCs.getOHLC(i+1).getHigh())))
    		{
    				if(filteredOHLCs.getOHLC(i-1).getLow()<filteredOHLCs.getOHLC(i+1).getLow())
    				{
    					filteredOHLCs.getOHLC(i).setLow(filteredOHLCs.getOHLC(i-1).getHigh());
    					filteredOHLCs.getOHLC(i).setHigh(filteredOHLCs.getOHLC(i+1).getLow());
    				}else
    				{
    					filteredOHLCs.getOHLC(i).setHigh(filteredOHLCs.getOHLC(i-1).getLow());
    					filteredOHLCs.getOHLC(i).setLow(filteredOHLCs.getOHLC(i-1).getHigh());
    				}
    				filteredOHLCs.getOHLC(i).setClose(filteredOHLCs.getOHLC(i+1).getOpen());
    				filteredOHLCs.getOHLC(i).setOpen(filteredOHLCs.getOHLC(i-1).getClose());
    				j++;
    			
        		}
        		}
    	System.out.println(filteredOHLCs.getNumberOfEntries());
    	System.out.println("Found outliers: "+j);
    	return filteredOHLCs;
    }

}

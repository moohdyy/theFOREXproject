package datacollection;

import java.util.ArrayList;
import strategies.AbstractStrategy;

public class CurrencyCourseOHLC {

    private String currencyPair;
    private int numberOfEntries;
    private int actualPosition;
    private ArrayList<OHLC> listOHLC;
    private double spread;

    public CurrencyCourseOHLC() {
        this.currencyPair = "NONE";
        this.numberOfEntries = 0;
        this.listOHLC = new ArrayList<>(numberOfEntries);
        this.actualPosition = 0;
    }

    public CurrencyCourseOHLC(String currencyPair, double spread) {
        this();
        this.spread = spread;
        this.currencyPair = currencyPair;
    }

    /**
     * @return the currencyPair
     */
    public String getCurrencyPair() {
        return currencyPair;
    }

    /**
     * @return the numberOfEntries
     */
    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public int addOHLC(OHLC ohlc){
        this.listOHLC.add(ohlc);
        this.numberOfEntries++;
        return this.listOHLC.indexOf(ohlc);
    }
    
    public OHLC getOHLC(int index){
        return this.listOHLC.get(index);
    }
    
    public long getTimeStamp(int index){
        return this.listOHLC.get(index).getTimestamp();
    }
    
    public double getOpen(int index){
        return this.listOHLC.get(index).getOpen();
    }
    
    public double getHigh(int index){
        return this.listOHLC.get(index).getHigh();
    }
    
    public double getLow(int index){
        return this.listOHLC.get(index).getLow();
    }
    
    public double getClose(int index){
        return this.listOHLC.get(index).getClose();
    }
    
    public double getAmount(int index){
        return this.listOHLC.get(index).getAmount();
    }
    
    public double getAskPrice(int index){
        return this.getClose(index);
    }
    
    public double getBidPrice(int index){
        return this.getClose(index) +this.spread;
    }

    /**
     * @return the actualPosition
     */
    public int getActualPosition() {
        return this.actualPosition;
    }
    
    public OHLC getOHLCOfActualPosition(){
        return this.listOHLC.get(actualPosition);
    }

    /**
     * @param actualPosition the actualPosition to set
     */
    public void setActualPosition(int actualPosition) {
        this.actualPosition = actualPosition;
    }
    
    
}

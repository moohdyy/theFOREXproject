/*
 * Tis is required later for multiple orders
 */
package simulation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import strategies.JapaneseCandlestick;
import strategies.JapaneseCandlestick.Patterns;
import strategies.JapaneseCandlesticksStrategy;
import Connection.Time;

/**
 *
 * @author Moohdyy
 */
public class Trade {

    public final static int BUY = 1;
    public final static int SELL = 2;
    public final static int NOACTION = 0;

    private int javaID;
    private int MT4ID;
    private final int tradeType;
    private long timeStampOpen;
    private double openingPrice;
    private final double volume;
    private long timeStampClose;
    private double stopLoss = -1;
    private double takeProfit = -1;
    private boolean open = true;
    private Patterns pattern=null;

    public Trade(int tradeType, double volume) {
        this.javaID = -1;
        this.MT4ID = -1;
        this.tradeType = tradeType;
        this.volume = volume;
    }

    public double getProfitOrLoss(double actualPrice) {
        return volume * (1 - openingPrice / actualPrice);
    }
    public void setPattern(Patterns pattern)
    {
    	this.pattern=pattern;
    }
    public double getProfitOrLossOld(double actualPrice) {
        switch (this.getTradeType()) {
            case Trade.BUY:
                return (actualPrice - openingPrice) * volume;
            case Trade.SELL:
                return (openingPrice - actualPrice) * volume;
            default:
                return 0;
        }
    }

    public double getVolume() {
        return this.volume;
    }

    /**
     * @return the tradeType
     */
    public int getTradeType() {
        return tradeType;
    }

    /**
     * @return the javaID
     */
    public int getJavaID() {
        return javaID;
    }

    /**
     * @param javaID the javaID to set
     */
    public void setJavaID(int javaID) {
        this.javaID = javaID;
    }

    /**
     * @return the MT4ID
     */
    public int getMT4ID() {
        return MT4ID;
    }

    /**
     * @param MT4ID the MT4ID to set
     */
    public void setMT4ID(int MT4ID) {
        this.MT4ID = MT4ID;
    }

    /**
     * @return the timeStampOpen
     */
    public long getTimeStampOpen() {
        return this.timeStampOpen;
    }

    /**
     * @return the openingPrice
     */
    public double getOpeningPrice() {
        return this.openingPrice;
    }

    /**
     * @return the timeStampClose
     */
    public long getTimeStampClose() {
        return this.timeStampClose;
    }

    /**
     * @param timeStampClose the timeStampClose to set
     */
    public void setTimeStampClose(long timeStampClose) {
        this.timeStampClose = timeStampClose;
    }

    /**
     * @return the stopLoss
     */
    public double getStopLoss() {
        return this.stopLoss;
    }

    /**
     * @param stopLoss the stopLoss to set
     */
    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public boolean hasStopLoss() {
        return this.stopLoss > 0;
    }

    /**
     * @return the takeProfit
     */
    public double getTakeProfit() {
        return takeProfit;
    }

    /**
     * @param takeProfit the takeProfit to set
     */
    public void setTakeProfit(double takeProfit) {
        this.takeProfit = takeProfit;
    }

    public boolean hasTakeProfit() {
        return this.takeProfit > 0;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void close() {
        this.open = false;
    }

    /**
     * @param timeStampOpen the timeStampOpen to set
     */
    public void setTimeStampOpen(long timeStampOpen) {
        this.timeStampOpen = timeStampOpen;
    }

    /**
     * @param openingPrice the openingPrice to set
     */
    public void setOpeningPrice(double openingPrice) {
        this.openingPrice = openingPrice;
    }
    
    
    public String getTradeTypeName(){
       return ((tradeType == BUY) ? "buy " :(tradeType == SELL)? "sell":"none");
    }
    public Patterns getPattern()
    {
    	return pattern;
    }
    public String toString()
    {
    	Date date=new Date(timeStampOpen);
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd;HH:mm");
    	String sT=sdf.format(date);
    	String type="";
    	if(this.tradeType==SELL)
    	{
    		type="SELL";
    	}else if(this.tradeType==BUY)
    	{
    		type="BUY";
    	}
    	String r=javaID+";"+sT+";"+type+ ";";
    	date=new Date(timeStampClose);
    	sT=sdf.format(date);
    	r+=sT;
    	System.out.println(pattern);
    	if(pattern!=null)
    	{
    		r+=";"+JapaneseCandlesticksStrategy.patternToString(pattern);
    	}
    	return r;
    }

}

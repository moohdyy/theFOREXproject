/*
 * Tis is required later for multiple orders
 */
package simulation;

/**
 *
 * @author Moohdyy
 */
public class Trade {

    public final static int BUY = 0;
    public final static int SELL = 1;
    public final static int NOACTION = -1;

    private int MT4ID;
    private final int tradeType;
    private long timeStampOpen;
    private double openingPrice;
    private final double volume;
    private long timeStampClose;
    private double stopLoss = 0;
    private double takeProfit = 0;
    private boolean open = true;

    public Trade(int tradeType, double volume) {
        this.MT4ID = 0;
        this.tradeType = tradeType;
        this.volume = volume;
    }

    public double getProfitOrLoss(double actualPrice) {
        return volume * (1 - openingPrice / actualPrice);
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

}

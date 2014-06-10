/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datacollection;

/**
 *
 * @author Moohdyy
 */
public class OHLC {
    private long timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double amount;

    
    
    public OHLC(){
        
    }
    
    
    public OHLC(long timestamp, double open, double high, double low, double close, double amount){
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.amount = amount;
    }
    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the open
     */
    public double getOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(double open) {
        this.open = open;
    }

    /**
     * @return the high
     */
    public double getHigh() {
        return high;
    }

    /**
     * @param high the high to set
     */
    public void setHigh(double high) {
        this.high = high;
    }

    /**
     * @return the low
     */
    public double getLow() {
        return low;
    }

    /**
     * @param low the low to set
     */
    public void setLow(double low) {
        this.low = low;
    }

    /**
     * @return the close
     */
    public double getClose() {
        return close;
    }

    /**
     * @param close the close to set
     */
    public void setClose(double close) {
        this.close = close;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
}

package datacollection;

public class CurrencyCourse {

    private String currencyPair;
    private int numberOfEntries;
    private long[] timeStamps;
    private double[] bidPrices;
    private double[] askPrices;

    public CurrencyCourse() {
        this.currencyPair = "NONE";
        this.numberOfEntries = 0;
        this.timeStamps = new long[numberOfEntries];
        this.askPrices = new double[numberOfEntries];
        this.bidPrices = new double[numberOfEntries];
    }

    public CurrencyCourse(String currencyPair, long[] timeStamp, double[] bidPrice, double[] askPrice) {
        this.currencyPair = currencyPair;
        this.numberOfEntries = bidPrice.length;
        this.timeStamps = timeStamp;
        this.askPrices = askPrice;
        this.bidPrices = bidPrice;
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

    /**
     * @return the timeStamp
     */
    public long[] getTimeStamps() {
        return timeStamps;
    }

    public long getTimeStamp(int index) {
        return this.timeStamps[index];
    }

    /**
     * @return the bidPrice
     */
    public double[] getBidPrices() {
        return bidPrices;
    }

    public double getBidPrice(int index) {
        return this.bidPrices[index];
    }

    /**
     * @return the askPrice
     */
    public double[] getAskPrices() {
        return askPrices;
    }

    public double getAskPrice(int index) {
        return this.askPrices[index];
    }
}

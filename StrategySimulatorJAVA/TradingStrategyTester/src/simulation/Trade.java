/*
 * Tis is required later for multiple orders
 */
package simulation;

import de.flohrit.mt4j.MT4BasicClientMINE;

/**
 *
 * @author Moohdyy
 */
public class Trade {

    private final int id;
    private final int tradeType;
    private final long timeStamp;
    private final double openingPrice;
    private final double volume;
    private boolean open = true;

    public Trade(int id, int tradeType, double openingPrice, double volume, long timeStamp) {
        this.id = id;
        this.openingPrice = openingPrice;
        this.tradeType = tradeType;
        this.timeStamp = timeStamp;
        this.volume = volume;
    }
    

    public double closeTrade(double closingPrice) {
        if (open) {
            open = false;
            return getProfitOrLoss(closingPrice);
        } else {
            throw new TradeException("Order was already closed");
        }
    }
    
    
    public double getProfitOrLoss(double actualPrice){
        switch (tradeType) {
                case MT4BasicClientMINE.PROCESS_TICK_DO_BUY:
                    return (actualPrice-openingPrice)*volume;
                case MT4BasicClientMINE.PROCESS_TICK_DO_SELL:
                    return (openingPrice-actualPrice)*volume;
                default:
                    return 0;
            }
    }
    
    public boolean isOpen(){
        return this.open;
    }
    
    public double getVolume(){
        return this.volume;
    }
    
}

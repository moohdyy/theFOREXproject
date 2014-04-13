/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;

import datacollection.CurrencyCourse;
import de.flohrit.mt4j.AbstractBasicClientMINE;
import de.flohrit.mt4j.MT4BasicClientMINE;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Moohdyy
 */
public class StrategySimulation {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private TradeManager tm;
    private AbstractBasicClientMINE strategy;
    private CurrencyCourse cc;
    private long actualTime = 0;


    public StrategySimulation(AbstractBasicClientMINE strategy, CurrencyCourse cc, double balance) {
        this.cc = cc;
        this.strategy = strategy;
        this.tm = new TradeManager(balance);
    }

    public StrategySimulation(AbstractBasicClientMINE strategy, CurrencyCourse cc, double balance, double leverage) {
        this(strategy, cc,balance);
        this.tm.setLeverage(leverage);
    }

    //for this simulation, the strategy just has one order at maximum open and can close a BUY order by returning SELL and vice versa
    public void simulateOneOrderStrategy() {
        int actualTradeID = -1;
        for (int index = 0; index < cc.getNumberOfEntries(); index++) {
            this.actualTime = cc.getTimeStamp(index);
            double actualPrice = cc.getBidPrice(index);
            if (tm.checkForMarginCall(actualPrice)) {
                System.out.println("MARGIN CALL");
                return;
            }
            int orderType = strategy.processTick(actualPrice, cc.getAskPrice(index));
            if (orderType != MT4BasicClientMINE.PROCESS_TICK_NONE) {
                if (actualTradeID == -1) { // first trade or no Trade open
                    actualTradeID = processNewTrade(orderType, index, actualTradeID);
                } else {
                    Trade actualTrade = tm.getTradeByID(actualTradeID);
                    if (actualTrade.isOpen()) {
                        tm.closeOrder(actualTradeID, cc.getBidPrice(index));
                        actualTradeID = -1;
                    } else {
                        actualTradeID = processNewTrade(orderType, index, actualTradeID);
                    }
                }
            }
            printCurrentStats(actualPrice);
        }
    }

    private int processNewTrade(int orderType, int index, int actualTradeID) {
        double actualPrice;
        if (orderType == MT4BasicClientMINE.PROCESS_TICK_DO_BUY) {
            actualPrice = cc.getAskPrice(index);
        } else { //MT4BasicClientMINE.PROCESS_TICK_DO_SELL
            actualPrice = cc.getBidPrice(index);
        }
        actualTradeID = tm.newOrder(orderType, actualPrice, strategy.getVolumeMINE(), this.actualTime);
        return actualTradeID;
    }

    public void printCurrentStats(double actualPrice) {
        System.out.println(sdf.format(new Date(actualTime))+":\n ActiveTrades: " + tm.getActiveTradesCount() + ", ClosedTrades: " + tm.getClosedTradesCount() + ", Balance: " + this.tm.getBalance() + ", Equity: " + this.tm.getEquity(actualPrice) + ", Margin: " + this.tm.getUsedMargin());
    }
    
}

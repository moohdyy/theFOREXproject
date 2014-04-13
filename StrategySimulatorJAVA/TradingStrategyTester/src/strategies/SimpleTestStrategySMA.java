/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strategies;

import datacollection.CurrencyCourse;
import de.flohrit.mt4j.*;
import java.util.HashMap;

/**
 *
 * @author Moohdyy
 */
public class SimpleTestStrategySMA extends AbstractBasicClientMINE {

    private HashMap<Long, String> SMAstrings;
    private CurrencyCourse cc;
    private int counter = 0;
    

    public SimpleTestStrategySMA(HashMap<Long, String> SMAstrings, CurrencyCourse cc) {
        this.SMAstrings = SMAstrings;
        this.cc = cc;
    }

    @Override
    public int processTick(double bid, double ask) {
        long actualTime = cc.getTimeStamp(counter);
        if(SMAstrings.get(actualTime).equals("rising")){
            return MT4BasicClientMINE.PROCESS_TICK_DO_BUY;
        }
        if(SMAstrings.get(actualTime).equals("falling")){
            return MT4BasicClientMINE.PROCESS_TICK_DO_SELL;
        }
        counter++;
        return PROCESS_TICK_NONE;
    }

    @Override
    public void init() {
        System.out.println("simpleTestStrategySMA init()");
    }

    @Override
    public void deinit() {
        System.out.println("simpleTestStrategySMA deinit()");
    }

}

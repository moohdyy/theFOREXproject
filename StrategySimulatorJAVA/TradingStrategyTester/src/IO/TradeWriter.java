/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package IO;

import java.util.List;
import simulation.Trade;

/**
 *
 * @author Moohdyy
 */
public class TradeWriter {
    private String filename;
    private List<Trade> trades;
    
    public TradeWriter(){
        
    }
    
    public TradeWriter(String filename){
        this.filename = filename;
    }
    
}

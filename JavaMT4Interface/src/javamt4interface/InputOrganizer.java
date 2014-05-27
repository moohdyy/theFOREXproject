/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javamt4interface;

import java.io.File;

/**
 *
 * @author Moohdyy
 */
public class InputOrganizer {
    File ohlcFile;
    File tradesFile;
    
    public InputOrganizer(){
        
    }

    public void setOHLCFile(File ohlcFile){
        this.ohlcFile = ohlcFile;
    }
    
    public void setTradesFile(File tradesFile){
        this.tradesFile = tradesFile;
    }
    public boolean isValid(){
        return ohlcFile!=null && tradesFile!=null;
    }
}

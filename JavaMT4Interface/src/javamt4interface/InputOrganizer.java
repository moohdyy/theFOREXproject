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
    private File ohlcFile;
    private File tradesFile;
    
    public InputOrganizer(){
        
    }
    
    public boolean isValid(){
        return getOhlcFile()!=null && getTradesFile()!=null;
    }

    /**
     * @return the ohlcFile
     */
    public File getOhlcFile() {
        return ohlcFile;
    }

    /**
     * @param ohlcFile the ohlcFile to set
     */
    public void setOhlcFile(File ohlcFile) {
        this.ohlcFile = ohlcFile;
    }

    /**
     * @return the tradesFile
     */
    public File getTradesFile() {
        return tradesFile;
    }

    /**
     * @param tradesFile the tradesFile to set
     */
    public void setTradesFile(File tradesFile) {
        this.tradesFile = tradesFile;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javamt4interface;

/**
 *
 * @author Moohdyy
 */
public class InputOrganizer {
    private String ohlcFileName;
    private String tradesFileName;
    
    public InputOrganizer(){
        ohlcFileName = "";
        tradesFileName = "";
    }
    
    public boolean isValid(){
        return !ohlcFileName.equals("") && !tradesFileName.equals("");
    }

    /**
     * @return the ohlcFile
     */
    public String getOhlcFileName() {
        return ohlcFileName;
    }

    /**
     * @param ohlcFile the ohlcFile to set
     */
    public void setOhlcFileName(String ohlcFile) {
        this.ohlcFileName = ohlcFile;
    }

    /**
     * @return the tradesFile
     */
    public String getTradesFileName() {
        return tradesFileName;
    }

    /**
     * @param tradesFile the tradesFile to set
     */
    public void setTradesFileName(String tradesFile) {
        this.tradesFileName = tradesFile;
    }
}

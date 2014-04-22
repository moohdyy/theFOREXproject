/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package IO;

import datacollection.CurrencyCourseOHLC;

/**
 *
 * @author Moohdyy
 */
public class CourseWriter {
    private CurrencyCourseOHLC cc;
    private String filename;
    
    public CourseWriter(String filename){
        this.filename = filename;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}

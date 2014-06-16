/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Moohdyy
 */
public class HistoricalDataFormatter {

    /**
     * @param args the command line arguments
     */
	public static int month=0;
    public static void main(String[] args) throws IOException, ParseException {
        String curr = "USDJPY";
        String year = "2014";
        String prefix = "historicalData\\"+curr+"\\"+year+"\\";
        for(int i=1;i<7;i++)
        {
        	month=i;
        String filename = prefix + "HISTDATA_COM_MT_"+curr+"_M120140"+i+".zip";
        
        createMonthlyDataFromFile(filename, prefix);
        }
    }

    private static void createMonthlyDataFromFile(String filename, String prefix) throws IOException, NumberFormatException, FileNotFoundException {
        File f=new File(filename);
        ZipFile zipF=new ZipFile(f);
        ZipInputStream zipIn=new ZipInputStream(new FileInputStream(f));
        ZipEntry zipEntry=zipIn.getNextEntry();
        while(!zipEntry.getName().contains("csv")&&zipEntry!=null)
        {
        	zipEntry=zipIn.getNextEntry();
        }
        if(zipEntry!=null)
        {
    	
        BufferedReader br = new BufferedReader(new InputStreamReader(zipF.getInputStream(zipEntry)));
        String line = br.readLine();
        // date format: 2012-01-02 02:00:02.183000000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Calendar cal = Calendar.getInstance();
        StringBuilder sb = new StringBuilder("");
        while (line!=null) {
            sb.append(line).append("\n");
            line=br.readLine();
        }
        writeToFile(sb, prefix, month);
        br.close();
        }
    }

    private static StringBuilder writeToFile(StringBuilder sb, String prefix, int lastMonth) throws IOException {
        sb.setLength(sb.length() - 1);
        File f = new File(prefix + lastMonth + ".csv");
        f.delete();
        try (FileWriter fw = new FileWriter(f, true)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString());
            bw.close();
        }
        sb = new StringBuilder("");
        return sb;
    }

    public static int count(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

}

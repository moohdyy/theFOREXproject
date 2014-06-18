/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package historicaldataformatter;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *  helper program to get monthly data out of a bigger file (like yearly)
 * @author Moohdyy
 */
public class HistoricalDataFormatter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException {
        String curr = "USDJPY";
        String year = "2012";
        String prefix = "historicalData\\"+curr+"\\"+year+"\\";
        String filename = prefix + "DAT_MT_"+curr+"_M1_"+year+".csv";
        
        createMonthlyDataFromFile(filename, prefix);
    }

    private static void createMonthlyDataFromFile(String filename, String prefix) throws IOException, NumberFormatException, FileNotFoundException {
        int numberOfLines = count(filename);
        FileInputStream fis = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        // date format: 2012-01-02 02:00:02.183000000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        int lastMonth = 1;
        int actualMonth = 1;
        Calendar cal = Calendar.getInstance();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < numberOfLines; i++) {

            line = br.readLine();
            actualMonth = Integer.parseInt(line.substring(5, 7));
            if (lastMonth != actualMonth) {
                sb = writeToFile(sb, prefix, lastMonth);
            }
            lastMonth = actualMonth;
            sb.append(line).append("\n");
        }
        writeToFile(sb, prefix, actualMonth);
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

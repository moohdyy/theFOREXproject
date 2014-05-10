package datacollection;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormatHistDataOHLCWithoutSpread extends CurrencyCourseCreator {

    @Override
    public CurrencyCourseOHLC getCurrencyCourseFromFile(String filename, String currencyPair) throws IOException, ParseException {
        int numberOfLines = count(filename);
        FileInputStream fis = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        String[] oneLine;
        // date format: 2012-01-02 02:00:02.183000000
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        long timeStamp;
        double open, high, low, close,amount;
        CurrencyCourseOHLC cc = new CurrencyCourseOHLC(currencyPair);
        for (int i = 0; i < numberOfLines; i++) {
            
            line = br.readLine();
            oneLine = line.split(",");
            // format for MT4 ohlc: 
            // Date, time, open, high, low, close, amount
//            System.out.println(oneLine[0] + ", " + oneLine[3] + ", " + oneLine[3].length());
            timeStamp = sdf.parse(oneLine[0]+" "+oneLine[1]).getTime();
            
            open = Double.parseDouble(oneLine[2]);
            high = Double.parseDouble(oneLine[3]);
            low = Double.parseDouble(oneLine[4]);
            close = Double.parseDouble(oneLine[5]);
            amount = Double.parseDouble(oneLine[6]);
            cc.addOHLC(new OHLC(timeStamp, open, high, low, close, amount));
        }
        return cc;
    }
}

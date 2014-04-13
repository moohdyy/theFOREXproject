package datacollection;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FormatGAINCapital extends CurrencyCourseCreator {

    @Override
    public CurrencyCourse getCurrencyCourseFromFile(String filename) throws IOException, ParseException {
        int numberOfLines = count(filename) - 1;
        long[] timeStamp = new long[numberOfLines];
        double[] bid = new double[numberOfLines];
        double[] ask = new double[numberOfLines];
        FileInputStream fis = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        String[] oneLine;
        br.readLine();// first line header
        String currencyPair = "";
        // GAIN date format: 2012-01-02 02:00:02.183000000
        SimpleDateFormat sdfWithNano = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat sdfWithoutNano = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < numberOfLines; i++) {
            line = br.readLine();
            oneLine = line.split(",");
            // format for GAIN capital: 
            // lTid,cDealable,CurrencyPair,RateDateTime,RateBid,RateAsk
            if (i == 0) {
                currencyPair = oneLine[2];
            }
//            System.out.println(oneLine[0] + ", " + oneLine[3] + ", " + oneLine[3].length());
            if (oneLine[3].length() == 29) {

                timeStamp[i] = sdfWithNano.parse(oneLine[3].substring(0, oneLine[3].length() - 6)).getTime();
            }else{
                timeStamp[i] = sdfWithoutNano.parse(oneLine[3].substring(0, oneLine[3].length())).getTime();
            }
            bid[i] = Double.parseDouble(oneLine[4]);
            ask[i] = Double.parseDouble(oneLine[5]);
        }
        return new CurrencyCourse(currencyPair, timeStamp, bid, ask);
    }
}

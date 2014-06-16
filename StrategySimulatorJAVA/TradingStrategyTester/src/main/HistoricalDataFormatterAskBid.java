package main;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Moohdyy
 */
public class HistoricalDataFormatterAskBid {

	/**
	 * @param args
	 *            the command line arguments
	 */
	static String[] currencyPairs = { "EURUSD", "GBPUSD", "USDJPY" };
	static String[] years = { "2011", "2012", "2014" };
	static int month = 0;

	public static void main(String[] args) throws IOException, ParseException {
		for (String curr : currencyPairs) {
			for (String year : years) {

				String prefix = "historicalData\\" + curr + "\\" + year + "\\";
				
					month = 1;
					String filename = prefix +  "BidAsk.csv";

					createMonthlyDataFromFile(filename, prefix);
					System.out.println(curr+" "+year);
				
			}
		}
	}

	private static void createMonthlyDataFromFile(String filename, String prefix)
			throws IOException, NumberFormatException, FileNotFoundException {
		int numberOfLines = count(filename);
		FileInputStream fis = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		// date format: 2012-01-02 02:00:02.183000000
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		int lastMonth = month;
		int actualMonth = month;
		Calendar cal = Calendar.getInstance();
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < numberOfLines; i++) {
			line = br.readLine();
			String[] lines = line.split(",");
			line = "";
			String[] date = lines[3].split(" ");
			line += date[0];
			String[] time = date[1].split("\\.");
			line += " " + time[0];
			for (int j = 4; j < lines.length; j++) {
				line += ";" + lines[j];
			}
			actualMonth = Integer.parseInt(line.substring(5, 7));
			if (lastMonth == actualMonth) {
				lastMonth = actualMonth;
				sb.append(line).append("\n");
			}else if(actualMonth>lastMonth)
			{
				writeToFile(sb,prefix,lastMonth);
				lastMonth=actualMonth;
				sb=new StringBuilder("");
				sb.append(line).append("\n");
			}

		}
		writeToFile(sb, prefix, lastMonth);
	}

	private static StringBuilder writeToFile(StringBuilder sb, String prefix,
			int lastMonth) throws IOException {
		sb.setLength(sb.length() - 1);
		File f = new File(prefix + "BidAsk_" + lastMonth + ".csv");
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

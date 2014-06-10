package Connection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import simulation.Trade;

public class Connector {
	File input = new File("");
	File output =new File("");
	Time lastTimestamp;
	Candlesticks candles = new Candlesticks();

	public void connection() {
		try {
			while(true)
			{
			boolean readInNext = true;
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			while (readInNext) {
				String next = br.readLine();
				if (next != null) {
					next=next.replace(" ", "");
					String[] candle = next.split(",");
					if (candle.length == 5) {
						Time time = new Time(candle[0],candle[1]);
						double start = Double.parseDouble(candle[2]);
						double end = Double.parseDouble(candle[3]);
						double low = Double.parseDouble(candle[4]);

						double high = Double.parseDouble(candle[4]);
						candles.add(new Candlestick(time, start, end, low, high));
						if (time.same(lastTimestamp)) {
							readInNext = false;
						} else {
							readInNext = true;
						}
					} else {
						readInNext = false;
					}
				} else {
					readInNext = false;
				}
				br.close();
				fr.close();
			}
			Thread.sleep(60000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void placeOrder(Trade t)
	{
		
		try {
			FileWriter fw = new FileWriter(output);
			BufferedWriter bw=new BufferedWriter(fw);
			String trade=t.getJavaID()+","+t.getMT4ID()+","+t.isOpen()+","+t.getTradeType()+","+t.getOpeningPrice()+","+t.getVolume()+";";
			bw.write(trade);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

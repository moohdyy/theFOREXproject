package hneat;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


public class CSV {
	public static List<List<Double>> processCSV(File fname)  //Assumes first 4 doubles are open,high,low,close
	{
		List<List<Double>> data = new ArrayList<List<Double>>(70);
		try {
			BufferedReader breader = new BufferedReader(new FileReader(fname));
			String row = breader.readLine();
			row = breader.readLine();		//Skip header line
			while(row != null) {
				List<Double> dataRow = new ArrayList<Double>(5);
				String[] splitRow = row.split(",");
				for(String item : splitRow) {
					try {
						double dataItem = Double.parseDouble(item);
						dataRow.add(dataItem);
					} catch(NumberFormatException e) {
						//Do nothing
					}
				}
				data.add(dataRow);
				row = breader.readLine();
			}
			breader.close();
		} catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return data;
	}
	
	public static List<List<Double>> processCSVSorted(File fname)  //Assumes first 4 doubles are open,high,low,close
	{
		List<List<Double>> data = new ArrayList<List<Double>>(70);
		try {
			BufferedReader breader = new BufferedReader(new FileReader(fname));
			String row = breader.readLine();
			row = breader.readLine();		//Skip header line
			List<Double> sortedRow;
			while(row != null) {
				List<Double> dataRow = new ArrayList<Double>(5);
				String[] splitRow = row.split(",");
				for(String item : splitRow) {
					try {
						double dataItem = Double.parseDouble(item);
						dataRow.add(dataItem);
					} catch(NumberFormatException e) {
						//Do nothing
					}
				}
				sortedRow = new ArrayList<Double>(4);
				sortedRow.add(dataRow.get(2));
				sortedRow.add(dataRow.get(1));
				if(dataRow.get(0)>dataRow.get(3)) {
					sortedRow.add(dataRow.get(3));
					sortedRow.add(dataRow.get(0));
				} else {
					sortedRow.add(dataRow.get(0));
					sortedRow.add(dataRow.get(3));
				}
				
				data.add(sortedRow);
				row = breader.readLine();
			}
			breader.close();
		} catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return data;
	}
	
	public static void createImages() {
		for (int choice=1249; choice<1250; choice++) {
			File file = new File("C:\\Users\\Maarten-work\\AppData\\Roaming\\MetaQuotes\\Terminal\\31B0DF029ADD8811005FA69713AE130D\\MQL4\\Files\\Data\\EURUSD60.csv_Pieces\\EURUSD60_"+choice+"_1.csv");
			file = new File("C:\\Users\\Maarten-work\\Documents\\2014Project2Forex\\weekendDiscontDataSample.csv");
			List<List<Double>> data = CSV.processCSV(file);
			
			
			List<List<Double>> sortedData = new ArrayList<List<Double>>(data.size());	//Sort to: low,high,low ending,high ending
			Double lowestLow = 1000.0;
			Double highestHigh = 0.0;
			for (List<Double> dataRow : data) {
				if(dataRow.get(2)<lowestLow) {
					lowestLow = dataRow.get(2);
				}
				if(dataRow.get(1)>highestHigh) {
					highestHigh = dataRow.get(1);
				}
				
				List<Double> sortedDataRow = new ArrayList<Double>(4);
				sortedDataRow.add(dataRow.get(2));
				sortedDataRow.add(dataRow.get(1));
				if(dataRow.get(0)<dataRow.get(3)) {
					sortedDataRow.add(dataRow.get(0));
					sortedDataRow.add(dataRow.get(3));
				} else {
					sortedDataRow.add(dataRow.get(3));
					sortedDataRow.add(dataRow.get(0));
				}
				sortedData.add(sortedDataRow);
			}
			
			BufferedImage bimage = new BufferedImage(70,50,BufferedImage.TYPE_USHORT_GRAY);
			Graphics2D g2d = bimage.createGraphics();
			g2d.setBackground(Color.WHITE);
			g2d.fillRect(0,0,70,50);
			Double scale = (highestHigh-lowestLow)/50;
			
			for (int i=0; i<sortedData.size(); i++) {
//				System.out.println("---------------------------------");
				List<Double> dr = sortedData.get(i);
				g2d.setColor(Color.GRAY);
				double jLow = (dr.get(0)-lowestLow)/scale;
				double jHigh = (dr.get(1)-highestHigh)/scale+50;
//				System.out.println(i+","+(int)Math.round(jLow)+","+(i)+","+(int)Math.round(jHigh));
//				System.out.println(dr.get(0)+","+dr.get(1)+","+dr.get(2)+","+dr.get(3)+","+lowestLow+","+highestHigh);
				
				g2d.fillRect(i,50-(int)Math.round(jHigh),1,(int)Math.round(jHigh)-(int)Math.round(jLow));
				
				g2d.setColor(Color.BLACK);
				jLow = (dr.get(2)-lowestLow)/scale;
				jHigh = (dr.get(3)-highestHigh)/scale+50;
//				System.out.println(i+","+(int)Math.round(jLow)+","+(i)+","+(int)Math.round(jHigh));
				
				//Todo: flip graph
				g2d.fillRect(i,50-(int)Math.round(jHigh),1,(int)Math.round(jHigh)-(int)Math.round(jLow));
			}
			System.out.println(sortedData.size());
			File oFile = new File("C:\\Users\\Maarten-work\\AppData\\Roaming\\MetaQuotes\\Terminal\\31B0DF029ADD8811005FA69713AE130D\\MQL4\\Files\\Data\\EURUSD60.csv_Pieces\\EURUSD60_"+choice+"_1.gif");
			oFile = new File("C:\\Users\\Maarten-work\\Documents\\2014Project2Forex\\weekendDiscontDataSample.gif");
			try {
				ImageIO.write(bimage,"gif",oFile);
			} catch(IOException e) {
				e.printStackTrace();
			}
			}	
	}
}

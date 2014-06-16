package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.sun.imageio.plugins.common.InputStreamAdapter;

public class GetDataFromInternet {
	static String[] currencyPairs = { "EUR_USD", "GBP_USD", "USD_JPY" };
	static String[] years = { "2011","2012","2014"};
	static String[] months={"01%20January","02%20February","03%20March","04%20April","05%20May","06%20June","07%20July","08%20August","09%20September","10%20October","11%20November","12%20December"};
	static String  urlD="http://ratedata.gaincapital.com/";

	public static void main(String[] args) {
	try{
			for (String curr : currencyPairs) {
				for (String year : years) {
				
					String prefix = "historicalData\\" + curr.replace("_", "") + "\\" + year
							+ "\\";
					BufferedWriter bw=new BufferedWriter(new FileWriter(prefix+"BidAsk.csv"));
					for (int i = 0; i < 12; i++) {
						String urlname=urlD+year+"/"+months[i]+"/";
						String filename = prefix + "DAT_MT_" + curr.replace("_","") + "_M" + i
								+ "_" + year + ".csv";
					
						URL url=null;
						String dataName="";
						for(int k=1;k<6;k++)
						{
							dataName=curr+"_Week"+k+".zip";
							url=new URL(urlname+dataName);
							HttpURLConnection huc =  ( HttpURLConnection )  url.openConnection (); 
							huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD"); 
							huc.connect () ; 
							int code = huc.getResponseCode() ;
							if(code!=404)
							{
								ZipInputStream zipIS=new ZipInputStream(url.openStream());
								ZipEntry zipE=zipIS.getNextEntry();
								dataName=curr+"_Week"+k+".csv";
								BufferedReader br=new BufferedReader(new InputStreamReader(zipIS));
								
								String l=br.readLine();
								l=br.readLine();
								while(l!=null)
								{
									bw.write(l+System.lineSeparator());
									l=br.readLine();
								}
								br.close();
								zipIS.close();
								
							}

					}
				}
					System.out.println("Finished: "+year+" "+curr);
					bw.close();
				
		} 
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		}
	
}

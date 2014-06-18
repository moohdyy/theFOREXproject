package hneat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;

import Connection.Candlestick;
import Connection.Time;
import strategies.JapaneseCandlestick;
import strategies.JapaneseCandlesticksStrategy.Trend;
import strategies.JapaneseCandlestick.Patterns;


public class CandlestickEvaluation {
	private final MLMethod phenotype;
	private CandlestickTrial testcase;
	private MLData output;
	private int testcaseNo;
	private double fitness = 0.0;
	public int count1 = 0;
	public int countM1 = 0;
	public int count0 = 0;
	

	public CandlestickEvaluation(MLMethod phenotype) {
		this.phenotype = phenotype;
	}
	
	public void generateNewTest() {
		this.testcase = new CandlestickTrial();
		this.testcaseNo = this.testcase.getCaseNo();
	}
	
	public IntDoublePair query() {
		File file = new File("C:\\Users\\Maarten Suurmond\\Dropbox\\Forex-EURUSD60\\EURUSD60_"+this.testcaseNo+".csv");
		List<List<Double>> csvData = CSV.processCSV(file);
		int vRes = testcase.getVertResolution();		
		MLData input = new BasicMLData(3);
		
//		Handle incorrect input
		if(csvData.size()!=49) {
			return new IntDoublePair(0,0);
		}
		
		//Find global low and high
		double min = 1000000.0;
		double max = 0.0;
		for (int i=0;i<csvData.size();i++) {
			if(csvData.get(i).get(0)<min) {
				min = csvData.get(i).get(0);
			}
			if(csvData.get(i).get(1)>max) {
				max = csvData.get(i).get(1);
			}
		}
		
		//Create the input image
//		for (int i=0; i<csvData.size();i++) {
//			for (int j=0; j<vRes; j++) {
//				int index = i*vRes+j;
//				if(((max-min)/vRes * j > (csvData.get(i).get(2)-min)) && ((max-min)/vRes * j < (csvData.get(i).get(3)-min))) {    //Test if between low and high ending
//					input.setData(index,1.0);
//				} else if(((max-min)/vRes * j > (csvData.get(i).get(0)-min)) && ((max-min)/vRes * j < (csvData.get(i).get(1)-min))) {    //Test if between period low and high
//					input.setData(index,0.0);
//				} else {
//					input.setData(index,-1.0);
//				}
//			}
//		}
		double sma49 = CandlestickEvaluation.getSMA(csvData, 49);
		double sma20 = CandlestickEvaluation.getSMA(csvData, 20);
		double sma7 = CandlestickEvaluation.getSMA(csvData, 7);
		ArrayList<JapaneseCandlestick> jsticks = new ArrayList<JapaneseCandlestick>(10);
		for(int i=11;i>0;i--) {
			double open = csvData.get(csvData.size()-i).get(0);
			double high = csvData.get(csvData.size()-i).get(1);
			double low = csvData.get(csvData.size()-i).get(2);
			double close = csvData.get(csvData.size()-i).get(3);
			Candlestick c = new Candlestick(new Time(0),open,high,low,close);
			JapaneseCandlestick jc = new JapaneseCandlestick(c);
			
			jsticks.add(jc);
		}
		
		Trend t1 = Trend.flat;
		if(sma49>sma20 && sma20>sma7) {
			t1 = Trend.falling;
		} else if(sma49<sma20 && sma20<sma7) {
			t1 = Trend.rising;
		}
		Trend t2 = Trend.flat;
		Patterns pattern = JapaneseCandlestick.determinePattern(jsticks,jsticks.size()-1,t1,t2);
		int i=0;
		while(i<4 && pattern == Patterns.None){
			i++;
			pattern = JapaneseCandlestick.determinePattern(jsticks,jsticks.size()-1-i,t1,t2);
		}
//		for(int i=0;i<14;i++) {
//			input.setData(i,0);
//		}
//		int patternIndex = pattern.ordinal()-1;
//		if(patternIndex >= 0) {
//			input.setData(patternIndex,1);
//		}
		boolean buy = JapaneseCandlestick.buyingSignal(pattern);
		boolean sell = JapaneseCandlestick.sellingSignal(pattern);
		input.setData(0,0);
		if(buy) {
			input.setData(0,1);
		} else if(sell) {
			input.setData(0,-1);
		}
		input.setData(1,0);
		if(t1 == Trend.falling) {
			input.setData(1,-1);
		} else if(t1 == Trend.rising) {
			input.setData(1,1);
		}
		int emaCrossover = determineEMACrossover(csvData,20,7);
		input.setData(2,emaCrossover);
//		int emaCrossover2 = determineEMACrossover(csvData,40,20);
//		input.setData(3,emaCrossover2);
//		
//		int smaCrossover = determineSMACrossover(csvData,20,7);
//		input.setData(4,smaCrossover);
//		int smaCrossover2 = determineSMACrossover(csvData,40,20);
//		input.setData(5,smaCrossover2);
//		System.out.println(pattern.ordinal()+","+input.getData(14)+","+input.getData(15));
		
		output = ((NEATNetwork)this.phenotype).compute(input);
			
		int output1 = this.convertDirectionToInt(output.getData(0));
		double output2 = output.getData(0);
		IntDoublePair result = new IntDoublePair(output1,output2);
		
		return result;
	}
	
	public int convertDirectionToInt(double d) {
		if(d>0.75) {
			return 1;
		} else if(d<0.25) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public double evaluateFitness(IntDoublePair trialResult) {
		File file = new File("C:\\Users\\Maarten Suurmond\\Dropbox\\Forex-EURUSD60\\EURUSD60_"+this.testcaseNo+"_1.csv");
		List<List<Double>> csvData = CSV.processCSV(file);    //open,high,low,close
		
		if(trialResult.intValue==-1) {
			this.countM1++;
		} else if(trialResult.intValue==0) {
			this.count0++;
		} else {
			this.count1++;
		}
		
		//Handle incorrect input
		if(csvData.size() != 70) {
			return 0.0;
		}
		
		//Find global low and high
		double min = 1000000.0;
		double max = 0.0;
		for (int i=0;i<csvData.size()-20;i++) {
			if(csvData.get(i).get(0)<min) {
				min = csvData.get(i).get(0);
			}
			if(csvData.get(i).get(1)>max) {
				max = csvData.get(i).get(1);
			}
		}
		
		double fitness = 0.0;
		
			int direction = trialResult.intValue;
			if(direction == 0) {
				return fitness;
			}
			double start = csvData.get(csvData.size()-21).get(3);
			double maxHighChange = 0.0;
			double maxWrongHighChange = 0.0;
			double maxLowChange = 0.0;
			double maxWrongLowChange = 0.0;
			int hIndex = 1;  //high
			int lIndex = 2;
			double highScore = 0.0;		
			double lowScore = 0.0;		
			
			//Assume stop-loss at 0.5 * expected change, allow partial score for too large predicted change
			for (int i=csvData.size()-20; i<csvData.size(); i++) {
				int modifier = 4-(i-csvData.size()+20)/5;
				if((csvData.get(i).get(hIndex)-start) > Math.min(maxHighChange,2*maxWrongHighChange)) {
					maxHighChange = (csvData.get(i).get(hIndex)-start);
					highScore += Math.pow(modifier,2)*Math.abs(maxHighChange)/(max-min);
				}
				if(start-(csvData.get(i).get(lIndex)) > maxWrongHighChange) {
					maxWrongHighChange = (start-csvData.get(i).get(lIndex));
				}
				
				if(start-(csvData.get(i).get(lIndex)) > Math.min(maxLowChange,2*maxWrongLowChange)) {
					maxLowChange = (start-csvData.get(i).get(lIndex));
					lowScore += Math.pow(modifier,2)*Math.abs(maxLowChange)/(max-min);
				}
				if((csvData.get(i).get(hIndex)-start) > maxWrongLowChange) {
					maxWrongLowChange = (csvData.get(i).get(hIndex)-start);
				}
			}
			
//			double fractionCorrect = maxHighChange / magnitude;
//			double fractionUnderestimated = 0.0;
//			if(fractionCorrect > 1) {
//				fractionCorrect = 1;
//				fractionUnderestimated = (maxHighChange-magnitude)/magnitude;
//				if(fractionUnderestimated > 0.8) {  //Prevent this from dominating
//					fractionUnderestimated = 0.8;
//				}
//				fractionUnderestimated = 0;
//			}
//			
//			//Create a [-1,1] scale fitness score, below threshold fractionCorrect is negative
//			fitness = Math.pow(fractionCorrect,2) - Math.pow(fractionUnderestimated,4);
//			double threshold = Math.pow(0.33,2);
//			fitness -= threshold;
//			if(fitness<0) {
//				fitness *= (1/threshold);
//			} else {
//				fitness *= (1/(1-threshold));
//			}
		
//		fitness = -Math.abs(0.5-trialResult.doubleValue);
		if(trialResult.intValue==1) {
			fitness = highScore/(highScore+lowScore)-0.5;
		} else {
			fitness = lowScore/(highScore+lowScore)-0.5;
		}
		
		return fitness;
	}
	
	public void addFitness(double fitness) {
		this.fitness += fitness;
	}
	public double getFitness() {
		//Penalise cases which overwhelmingly choose 1 option
		double percM1 = (double)countM1/(count1+count0+countM1);
		double perc0 = (double)count0/(count1+count0+countM1);
		double perc1 = (double)count1/(count1+count0+countM1);
		double perc = percM1;
		if(perc0>perc) {
			perc = perc0;
		}
		if(perc1>perc) {
			perc = perc1;
		}
		perc = 1-perc;		
		
		double fitness = this.fitness;
		if(perc==0) {		
			fitness = -100;
		} else if(perc<0.25) {
			perc *= 4;
			fitness -= (1-perc)*Math.abs(fitness);
		}
		
		return fitness;
	}
	
	public static double getSMA(List<List<Double>> ohlc,int n) {
		if(ohlc.size()<n) {
			return 0;
		}
		double total = 0.0;
		for(int i=1;i<n+1;i++) {
			double midpoint = ohlc.get(ohlc.size()-i).get(0)+ohlc.get(ohlc.size()-i).get(3);
			midpoint /= 2;
			total += midpoint;
		}
		return total/n;
	}
	public static double getEMA(List<List<Double>> ohlc, int n) {
		int divideBy = (n*(n+1))/2;
		if(ohlc.size()<n) {
			return 0;
		}
		double total = 0.0;
		for(int i=1;i<n+1;i++) {
			double midpoint = ohlc.get(ohlc.size()-i).get(0)+ohlc.get(ohlc.size()-i).get(3);
			midpoint /= 2;
			total += midpoint*(n+1-i)/divideBy;
		}
		return total;
	}
	
	//Determine if the longPeriod and shortPeriod EMA's cross each other in the last 5 periods
	public static int determineEMACrossover(List<List<Double>> ohlc,int longPeriod,int shortPeriod) {
		List<List<Double>> ohlc2 = new ArrayList<List<Double>>(ohlc);
		if(ohlc.size()<longPeriod+5) {
			return 0;
		}
		List<Double> longEMAs = new ArrayList<Double>(5);
		List<Double> shortEMAs = new ArrayList<Double>(5);
		for(int i=0;i<5;i++) {
			double longEma = getEMA(ohlc2,longPeriod);
			double shortEma = getEMA(ohlc2,shortPeriod);
			longEMAs.add(longEma);
			shortEMAs.add(shortEma);
			ohlc2 = ohlc2.subList(0, ohlc2.size()-2);
		}
		int mod=1;
		if(longEMAs.get(0)<shortEMAs.get(0)) {
			mod = -1;
		}
		
		int result = 0;
		for(int i=1;i<longEMAs.size();i++) {
			if(longEMAs.get(i)*mod>shortEMAs.get(i)*mod) {
				result = mod;
			}
		}
		return result;
	}
	//Determine if the longPeriod and shortPeriod SMA's cross each other in the last 5 periods
		public static int determineSMACrossover(List<List<Double>> ohlc,int longPeriod,int shortPeriod) {
			List<List<Double>> ohlc2 = new ArrayList<List<Double>>(ohlc);
			if(ohlc.size()<longPeriod+5) {
				return 0;
			}
			List<Double> longSMAs = new ArrayList<Double>(5);
			List<Double> shortSMAs = new ArrayList<Double>(5);
			for(int i=0;i<5;i++) {
				double longSma = getSMA(ohlc2,longPeriod);
				double shortSma = getSMA(ohlc2,shortPeriod);
				longSMAs.add(longSma);
				shortSMAs.add(shortSma);
				ohlc2 = ohlc2.subList(0, ohlc2.size()-2);
			}
			int mod=1;
			if(longSMAs.get(0)<shortSMAs.get(0)) {
				mod = -1;
			}
			
			int result = 0;
			for(int i=1;i<longSMAs.size();i++) {
				if(longSMAs.get(i)*mod>shortSMAs.get(i)*mod) {
					result = mod;
				}
			}
			return result;
		}

}

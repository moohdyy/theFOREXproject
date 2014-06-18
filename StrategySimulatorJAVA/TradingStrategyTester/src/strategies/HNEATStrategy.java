package strategies;

import hneat.CandlestickEvaluation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATPopulation;

import Connection.Candlestick;
import Connection.Time;
import simulation.Trade;
import datacollection.CurrencyCourseOHLC;

public class HNEATStrategy extends AbstractStrategy {
	private NEATPopulation network;
	private int vRes = 50;
	private int buySellStrength = 0;
	private int consBuySell = 0;
	
	public HNEATStrategy(CurrencyCourseOHLC ccOHLC, String genomeFilename) {
		super(ccOHLC, "HyperNEATStrategy");
		this.network = null;
		try {			
			InputStream is = new FileInputStream("NNetworks\\HNEAT49-EURUSD1-pop99-3");
			ObjectInputStream ois = new ObjectInputStream(is);
			this.network = (NEATPopulation)ois.readObject();
			ois.close();
			is.close();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public List<Trade> processNewCourse(List<Trade> actualTrades,
			CurrencyCourseOHLC currencyCourse) {
		// TODO Auto-generated method stub
		if(currencyCourse.getActualPosition()<50) {
			return actualTrades;
		}
		
		MLData input = new BasicMLData(3);
		List<List<Double>> csvData = new ArrayList<List<Double>>(49);
		
		int ap = currencyCourse.getActualPosition();
		for(int i=0;i<49;i++) {
			double open = currencyCourse.getOpen(ap-i);
			double high = currencyCourse.getHigh(ap-i);
			double low = currencyCourse.getLow(ap-i);
			double close = currencyCourse.getClose(ap-i);
			double lowE = open;
			double highE = close;
			if(lowE>highE) {
				lowE = close;
				highE = open;
			}
			
			List<Double> l = new ArrayList<Double>();
			l.add(low);
			l.add(high);
			l.add(lowE);
			l.add(highE);
			csvData.add(l);
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
		
		JapaneseCandlesticksStrategy.Trend t1 = JapaneseCandlesticksStrategy.Trend.flat;
		if(sma49>sma20 && sma20>sma7) {
			t1 = JapaneseCandlesticksStrategy.Trend.falling;
		} else if(sma49<sma20 && sma20<sma7) {
			t1 = JapaneseCandlesticksStrategy.Trend.rising;
		}
		JapaneseCandlesticksStrategy.Trend t2 = JapaneseCandlesticksStrategy.Trend.flat;
		JapaneseCandlestick.Patterns pattern = JapaneseCandlestick.determinePattern(jsticks,jsticks.size()-1,t1,t2);
		int i=0;
//		while(i<4 && pattern == JapaneseCandlestick.Patterns.None){
//			i++;
//			pattern = JapaneseCandlestick.determinePattern(jsticks,jsticks.size()-1-i,t1,t2);
//		}
		
		int patternBuy = 0;
		if(JapaneseCandlestick.buyingSignal(pattern)) {
			patternBuy = 1;
		} else if(JapaneseCandlestick.sellingSignal(pattern)) {
			patternBuy = -1;
		}
//		for(int i=0;i<14;i++) {
//			input.setData(i,0);
//		}
//		int patternIndex = pattern.ordinal()-1;
//		if(patternIndex >= 0) {
//			input.setData(patternIndex,1);
//		}
		input.setData(0,0);
		if(patternBuy==1) {
			input.setData(0,1);
		} else if(patternBuy==-1) {
			input.setData(0,-1);
		}
		input.setData(1,0);
		if(t1 == JapaneseCandlesticksStrategy.Trend.falling) {
			input.setData(1,-1);
		} else if(t1 == JapaneseCandlesticksStrategy.Trend.rising) {
			input.setData(1,1);
		}
		int emaCrossover = hneat.CandlestickEvaluation.determineEMACrossover(csvData,20,7);
		int smaCrossover = hneat.CandlestickEvaluation.determineSMACrossover(csvData,20,7);
		int emaCrossover2 = hneat.CandlestickEvaluation.determineEMACrossover(csvData, 40, 20);
		int smaCrossover2 = hneat.CandlestickEvaluation.determineSMACrossover(csvData, 40, 20);
		input.setData(2,emaCrossover);
//		input.setData(3,emaCrossover2);
//		input.setData(4,smaCrossover);
//		input.setData(5,smaCrossover2);
		
		MLMethod phenotype = network;

//		int total = 0;
//		int totalPatterns = 0;
//		for(int i=0;i<input.size();i++) {
//			total+=input.getData(i);
//			if(i<14) {
//				totalPatterns += input.getData(i);
//			}
//		}
//		if(total==0) {
//			return actualTrades;
//		}
		
		MLData output = ((NEATPopulation)phenotype).compute(input);
		int predictedMovement = this.convertDirectionToInt(output.getData(0));
		
		if(predictedMovement==0) {
			return actualTrades;
		}
		
		if(predictedMovement==1) {
			if(this.consBuySell>=0) {
				this.consBuySell++;
			} else {
				this.consBuySell = 1;
			}
		} else if(predictedMovement==-1) {
			if(this.consBuySell<=0) {
				this.consBuySell--;
			} else {
				this.consBuySell = -1;
			}
		}
		
		
		this.buySellStrength += predictedMovement*4;
		if(this.buySellStrength>2) {
			this.buySellStrength = 2;
		} else if(this.buySellStrength<-2) {
			this.buySellStrength = -2;
		}
		
//		//Added
//		this.buySellStrength += patternBuy-smaCrossover-emaCrossover;
//		if(this.buySellStrength>2) {
//			this.buySellStrength = 2;
//		} else if(this.buySellStrength<-2) {
//			this.buySellStrength = -2;
//		}
		
		if(predictedMovement == -1) {
			predictedMovement = Trade.SELL;
		} else if(predictedMovement == 1) {
			predictedMovement = Trade.BUY;
		}
		
		boolean isBuying = true;
		int activeTrades = 0;
		for(Trade trade : actualTrades) {
			if(trade.isOpen()) {
				activeTrades++;
				if(trade.getTradeType() == Trade.SELL){
					isBuying = false;
				}
			}
		}
		
		if(activeTrades>0) {
			if(this.buySellStrength>=0 && !isBuying) {
//				int tradesToClose = this.buySellStrength;
				int tradesToClose = 1;
				for(Trade trade : actualTrades) {
					if(trade.isOpen()) {
						trade.close();
						tradesToClose--;
						if(tradesToClose==0) {
							break;
						}
					}
				}
			} else if(this.buySellStrength<=0 && isBuying) {
//				int tradesToClose = -this.buySellStrength;
				int tradesToClose = 1;
				for(Trade trade : actualTrades) {
					if(trade.isOpen()) {
						trade.close();
						tradesToClose--;
						if(tradesToClose==0) {
							break;
						}
					}
				}
			}
		}
		
		if(this.buySellStrength>1 && predictedMovement == Trade.BUY) {
			if(activeTrades>0 && !isBuying) {
//				for(Trade trade : actualTrades) {
//					if(trade.isOpen()) {
//						trade.close();
//						break;
//					}
//				}
			} else if(activeTrades<3) {
				Trade t = new Trade(Trade.BUY,500);
				double stopLossMargin = 0.33*(max-min);
				t.setStopLoss(csvData.get(csvData.size()-1).get(3)-stopLossMargin);
				actualTrades.add(t);
			}
		} else if(this.buySellStrength<-1 && predictedMovement == Trade.SELL) {
			if(activeTrades>0 && isBuying) {
//				for(Trade trade : actualTrades) {
//					if(trade.isOpen()) {
//						trade.close();
//						break;
//					}
//				}
			} else if(activeTrades<3) {
				Trade t = new Trade(Trade.SELL,500);
				double stopLossMargin = 0.33*(max-min);
				t.setStopLoss(csvData.get(csvData.size()-1).get(3)+stopLossMargin);
				actualTrades.add(t);
			}
		}
		
		return actualTrades;
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

}

package indicators;

import datacollection.CurrencyCourseOHLC;

import java.util.ArrayList;

// source: https://code.google.com/p/adaptive-trading-system/source/browse/ATS/src/ats/strategies/Strategies.java?r=11
// Simple moving average
public class CopyOfSMA {
	private ArrayList<Double> values = new ArrayList<Double>();
	private int SMADuration=0;

	public CopyOfSMA(CurrencyCourseOHLC cc, int SMADuration) {
		int actualPosition=cc.getActualPosition();
		this.SMADuration=SMADuration;
		for(int i=SMADuration-1;i<cc.getNumberOfEntries();i++)
		{
			cc.setActualPosition(i);
			values.add(calculateSMA(cc));
		}
		cc.setActualPosition(actualPosition);
	}
	
	// Get simple moving average
	public double get(int i)
	{
		return values.get(i);
	}
	public double calculateSMA(CurrencyCourseOHLC cc) {
		if ((values.size()-1) < cc.getActualPosition()) {
			int lastIndex = cc.getActualPosition();
			int start = lastIndex - SMADuration + 1;
			double r = 0.0;
			for (int i = start; i <= lastIndex; i++) {
				r += cc.getOHLC(i).getClose();
			}
			r /= SMADuration * 1.0;
			return r;
		} else {
			return values.get(cc.getActualPosition());
		}
	}

}

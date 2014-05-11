package strategies;
import Connection.Candlestick;
import Connection.Time;

import java.util.ArrayList;
import java.util.Collections;
public class JapaneseCandlestick implements Comparable<JapaneseCandlestick> {
private double startingValue;
private double endingValue;
private double lowestValue;
private double lowerBody;
private double upperBody;
private double highestValue;
private Time time;
enum Categories{None,Bullish,Bearish};
enum Types{None,SpinningTop,WhiteMarubozu,BlackMarubozu,LongLeggedDoji,DragonflyDoji,GravestoneDoji,FourPriceDoji,Hammer,InvertedHammer};
enum Patterns{None,Hammer,HangingMan,InvertedHammer,ShootingStar,BullishEngulfing,BearishEngulfing,TweezerTops,TweezerBottoms,EveningStar,MorningStar,ThreeWhiteSoldiers,ThreeBlackCrows,ThreeInsideUp,ThreeInsideDown};
private Categories category=Categories.None;
private Types type=Types.None;
private Patterns pattern=Patterns.None;

public JapaneseCandlestick(Candlestick c) 
{
	this.startingValue=c.getOpening();
	this.endingValue=c.getClosing();
	this.highestValue=c.getHigh();
	this.lowestValue=c.getLow();
	this.time=c.getTimestamp();
	if(startingValue>endingValue)
	{
		this.upperBody=startingValue;
		this.lowerBody=endingValue;
	}else
	{
		this.upperBody=endingValue;
		this.lowerBody=startingValue;
	}
	category=determineCategory();
	type=determineType();
}

public Categories determineCategory()
{
	if(endingValue>startingValue)
	{
		return Categories.Bullish;
	}else if(endingValue<startingValue)
	{
		return Categories.Bearish;
	}
	return Categories.None;
}
public Types determineType()
{
	if(category==Categories.None)
	{
		if(highestValue==lowestValue)
		{
			return Types.FourPriceDoji;
		}else if(lowestValue==startingValue)
		{
			return Types.GravestoneDoji;
		}else if(highestValue==startingValue)
		{
			return Types.DragonflyDoji;
		}else
		{
			return Types.LongLeggedDoji;
		}
	}else if((highestValue==startingValue)&&(lowestValue==endingValue))
	{
		return Types.BlackMarubozu;
	}
	else if((highestValue==endingValue)&&(lowestValue==startingValue))
	{
		return Types.WhiteMarubozu;
	}
	double topStick;
	double bottomStick;
	if(endingValue>startingValue)
	{
		topStick=highestValue-endingValue;
		bottomStick=startingValue-lowestValue;
	}else
	{
		topStick=highestValue-startingValue;
		bottomStick=endingValue-lowestValue;
	}
	double body=Math.abs(startingValue-endingValue);
	if(((body*2)<=topStick)&&(body>bottomStick))
	{
		return Types.Hammer;
	}else if(((body*2)<=bottomStick)&&(body>topStick))
	{
		return Types.InvertedHammer;
	}
	if((body>topStick)&&(body>bottomStick))
	{
		return Types.SpinningTop;
	}
	return Types.None;
}
public static Patterns determinePattern(ArrayList<JapaneseCandlestick>candlesticks)
{
	int i=candlesticks.size()-1;
	Collections.sort(candlesticks);
		if(i>0)
		{
		if(candlesticks.get(i).type==Types.Hammer)
		{

				if(candlesticks.get(i-1).lowestValue>=candlesticks.get(i).upperBody)
				{
				
					if(hasDowntrend(candlesticks, i))
					{
						return Patterns.Hammer;
					}
					
				}else if(candlesticks.get(i-1).highestValue<=candlesticks.get(i).lowerBody)
				{
					if(hasUptrend(candlesticks, i))
					{
					return Patterns.HangingMan;
					}
					
				}
		}else if(candlesticks.get(i).type==Types.InvertedHammer)
		{
			if(candlesticks.get(i-1).lowestValue>=candlesticks.get(i).upperBody)
			{
			
				if(hasDowntrend(candlesticks, i))
				{
				return Patterns.InvertedHammer;
				}
				
			}else if(candlesticks.get(i-1).highestValue<=candlesticks.get(i).lowerBody)
			{
				if(hasUptrend(candlesticks, i))
				{
				return Patterns.ShootingStar;
				}
				
			}
		}
		double dif=candlesticks.get(i).highestValue-candlesticks.get(i).lowestValue;
		dif*=0.05;
		if(hasDowntrend(candlesticks, i-1))
		{
		if(candlesticks.get(i).category==Categories.Bullish)
		{
			if(candlesticks.get(i-1).category==Categories.Bearish)
			{
				if(Math.abs(candlesticks.get(i-1).lowerBody-candlesticks.get(i).lowerBody)<=Math.abs(dif))
				{
					double downStick=candlesticks.get(i-1).lowerBody-candlesticks.get(i-1).lowestValue;
					double downStick1=candlesticks.get(i).lowerBody-candlesticks.get(i).lowestValue;
					if(Math.abs(downStick-downStick1)<=dif)
					{
						return Patterns.TweezerBottoms;
					}else
					{
					return Patterns.BullishEngulfing;
					}
				}
			}
		}
		}
		else if(hasUptrend(candlesticks, i-1))
		{
		if(candlesticks.get(i-1).category==Categories.Bullish)
		{
			if(candlesticks.get(i).category==Categories.Bearish)
			{
				if(Math.abs(candlesticks.get(i-1).upperBody-candlesticks.get(i).upperBody)<=Math.abs(dif))
				{
					double upperStick=candlesticks.get(i-1).upperBody-candlesticks.get(i-1).lowestValue;
					double upperStick1=candlesticks.get(i).upperBody-candlesticks.get(i).lowestValue;
					if(Math.abs(upperStick-upperStick1)<=dif)
					{
						return Patterns.TweezerTops;
					}else
					{
					return Patterns.BearishEngulfing;
					}
				}
			}
		}
		
	}
		}
	 if(i>0)
	 {
		 
		 if(candlesticks.get(i-1).type==Types.SpinningTop)
		 {
			 if(hasDowntrend(candlesticks, i-2))
			 {
			 if(candlesticks.get(i-2).category==Categories.Bearish)
			 {
				 double middle=candlesticks.get(i-2).highestValue-candlesticks.get(i-2).lowestValue;
				 middle/=2;
				 middle+=candlesticks.get(i-2).lowestValue;
				 if(candlesticks.get(i).endingValue>middle)
				 {
					return Patterns.MorningStar; 
				 }
			 }
			 }else if(hasUptrend(candlesticks, i-2))
			 {
			 if(candlesticks.get(i-2).category==Categories.Bullish)
			 {
				 double middle=candlesticks.get(i-2).highestValue-candlesticks.get(i-2).lowestValue;
				 middle/=2;
				 middle+=candlesticks.get(i-2).lowestValue;
				 if(candlesticks.get(i).endingValue>middle)
				 {
					 return Patterns.EveningStar;
				 }
			 }
			 }
		 }
		 JapaneseCandlestick c=candlesticks.get(i-2);
		 double value=c.upperBody-c.lowerBody;
		 if(value>(c.highestValue-c.lowestValue))
		 {
			 if(hasDowntrend(candlesticks, i-2))
			 {
			 if(c.category==Categories.Bearish)
			 {
				double midpointC=c.highestValue-c.lowestValue;
				midpointC/=2;
				midpointC+=c.lowestValue;
				if(midpointC<candlesticks.get(i-1).endingValue)
				{
					if(candlesticks.get(i).endingValue>candlesticks.get(i-1).highestValue)
					{
						return Patterns.ThreeInsideUp; 
					}
				}
			 }
			 }
			 else if(hasUptrend(candlesticks, i-2))
			 {
			 
			 if(c.category==Categories.Bullish)
			 {
				double midpointC=c.highestValue-c.lowestValue;
				midpointC/=2;
				midpointC+=c.lowestValue;
				if(midpointC>candlesticks.get(i-1).endingValue)
				{
					if(candlesticks.get(i).endingValue<candlesticks.get(i-1).lowestValue)
					{
						return Patterns.ThreeInsideDown; 
					}
				}
			 }
			 }
		 }
		 
		 JapaneseCandlestick front=candlesticks.get(i);
		 JapaneseCandlestick middle=candlesticks.get(i-1);
		 JapaneseCandlestick back=candlesticks.get(i-2);
		if(hasDowntrend(candlesticks, i-3))
		{
		 if((front.category==Categories.Bullish)&&(middle.category==Categories.Bullish)&&(back.category==Categories.Bullish))
		 {
			if((back.getBodySize()<=middle.getSize())&&(middle.hasSmallUpperShadow()))
			{
				if(middle.getBodySize()<=front.getSize())
				{
					if(front.hasSmallUpperShadow())
					{
						return Patterns.ThreeWhiteSoldiers;
					}
				}
			}
		 }
		}
		else if(hasUptrend(candlesticks, i-3))
		{
		 if((front.category==Categories.Bearish)&&(middle.category==Categories.Bearish)&&(back.category==Categories.Bearish))
		 {
				if((back.getBodySize()<=middle.getSize())&&(middle.smallLowerShadow()))
				{
					if(middle.getBodySize()<=front.getSize())
					{
						if(front.smallLowerShadow())
						{
							return Patterns.ThreeWhiteSoldiers;
						}
					}
				}
		 }
		}
	 }
	return Patterns.None;
}
public double getSize()
{
	return this.highestValue-this.lowestValue;
}
public double getBodySize()
{
	return (upperBody-lowerBody);
}
public Time getTime()
{
	return this.time;
}
private boolean hasSmallUpperShadow(double percentage)
{
	double upperShadow=highestValue-upperBody;
	double sizeBodyP=upperBody-lowerBody;
	sizeBodyP*=percentage;
	if(upperShadow<sizeBodyP)
	{
		return true;
	}
	return false;
}
private boolean hasSmallLowerShadow(double percentage)
{
	double lowerShadow=lowerBody-lowestValue;
	double sizeBodyP=upperBody-lowerBody;
	sizeBodyP*=percentage;
	if(lowerShadow<sizeBodyP)
	{
		return true;
	}
	return false;
}
public boolean hasSmallUpperShadow()
{
	return hasSmallUpperShadow((1/3));
}
public boolean smallLowerShadow()
{
	return hasSmallLowerShadow((1/3));
}
public boolean hasNoUpperShadow()
{
	return hasSmallUpperShadow(0.001);
}
public boolean hasNoLowerShadow()
{
	return hasSmallLowerShadow(0.001);
}
public double getSizeUpperShadow()
{
	return (highestValue-upperBody);
}
public double getSizeLowerShadow()
{
	return (lowerBody-lowestValue);
}
public static boolean hasDowntrend(ArrayList<JapaneseCandlestick> candles,int i)
{
	ArrayList<Double> middlepoints=new ArrayList<Double>();
	for(int j=i-1;j>=0;j--)
	{
		middlepoints.add((candles.get(j).highestValue-candles.get(j).lowestValue)/2);
	}
	int j=0;
	while((j<middlepoints.size()-1)&&(middlepoints.get(j)<middlepoints.get(j+1)))
	{
		j++;
	}
	if(j>=5)
	{
		return true;
	}
	return false;
}

public static boolean hasUptrend(ArrayList<JapaneseCandlestick> candles, int i)
{
	ArrayList<Double> middlepoints=new ArrayList<Double>();
	for(int j=i-1;j>=0;j--)
	{
		middlepoints.add((candles.get(j).highestValue-candles.get(j).lowestValue)/2);
	}
	int j=0;
	while((j<middlepoints.size()-1)&&(middlepoints.get(j)>middlepoints.get(j+1)))
	{
		j++;
	}
	if(j>=5)
	{
		return true;
	}
	return false;
}
public static boolean buyingSignal(Patterns pattern)
{
	if(pattern==Patterns.BullishEngulfing)
	{
		return true;
	}else if (pattern==Patterns.TweezerBottoms)
	{
		return true;
	}else if(pattern==Patterns.MorningStar)
	{
		return true;
	}else if(pattern==Patterns.ThreeWhiteSoldiers)
	{
		return true;
	}else if(pattern==Patterns.ThreeInsideUp)
	{
		return true;
	}
	return false;
}
public static boolean sellingSignal(Patterns pattern)
{
	
	if(pattern==Patterns.BearishEngulfing)
	{
		return true;
	}else if(pattern==Patterns.TweezerTops)
	{
		return true;
	}else if(pattern==Patterns.EveningStar)
	{
		return true;
	}else if(pattern==Patterns.ThreeBlackCrows)
	{
		return true;
	}else if(pattern==Patterns.ThreeInsideDown)
	{
		return true;
	}
	return false;
}

public double getStartingValue() {
	return startingValue;
}
public double getEndingValue() {
	return endingValue;
}
public double getLowestValue() {
	return lowestValue;
}
public double getLowerBody() {
	return lowerBody;
}
public double getUpperBody() {
	return upperBody;
}
public double getHighestValue() {
	return highestValue;
}
public Categories getCategory() {
	return category;
}
public Types getType() {
	return type;
}
public Patterns getPattern() {
	return pattern;
}

@Override
public int compareTo(JapaneseCandlestick arg0) {
	// TODO Auto-generated method stub
	if(this.time.getTime()>arg0.getTime().getTime())
	{
		return 1;
	}else if(this.time.getTime()<arg0.getTime().getTime())
	{
		return -1;
	}
	return time.compareTo(arg0.getTime());
}
}

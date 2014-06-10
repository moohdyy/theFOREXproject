package Connection;
import java.util.ArrayList;


public class Candlesticks {
ArrayList<Candlestick> candlesticks=new ArrayList<>();
public void add(Candlestick c)
{
	candlesticks.add(c);
}
public void add(ArrayList<Candlestick> c)
{
	for(Candlestick i:c)
	{
		candlesticks.add(i);
	}
}
}

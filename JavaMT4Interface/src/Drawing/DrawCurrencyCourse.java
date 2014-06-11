package Drawing;

import forexstrategies.JapaneseCandlestick;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class DrawCurrencyCourse {
public DrawCurrencyCourse(ArrayList<JapaneseCandlestick> candles, String filename)
{
	double low=Double.MAX_VALUE;
	double high=Double.MIN_VALUE;
	for(int i=0;i<candles.size();i++)
	{
		if(candles.get(i).getLowestValue()<low)
		{
			low=candles.get(i).getLowestValue();
		}
		if(candles.get(i).getHighestValue()>high)
		{
			high=candles.get(i).getHighestValue();
		}
	}
	double multiplicator=3000/(high-low);
	multiplicator=Math.round(multiplicator);
	System.out.println(multiplicator);
	BufferedImage bi=new BufferedImage(candles.size(),3000, BufferedImage.TYPE_INT_BGR);
	Graphics g =bi.getGraphics();
	g.fillRect(0, 0, candles.size(),3000);
	for(int i=0;i<candles.size();i++)
	{
		double buttom=candles.get(i).getLowestValue()-low;
		buttom*=multiplicator;
	
		double top=candles.get(i).getHighestValue()-low;
		top*=multiplicator;
		g.setColor(Color.DARK_GRAY);
		g.drawLine(i, (int)(buttom), i, (int)(top));
		if(candles.get(i).getColor()==JapaneseCandlestick.Colors.Black)
		{
			g.setColor(Color.BLACK);
		}else if(candles.get(i).getColor()==JapaneseCandlestick.Colors.White)
		{
			g.setColor(Color.LIGHT_GRAY);
		}
		buttom=candles.get(i).getOpeningValue()-low;
		buttom*=multiplicator;
		top=candles.get(i).getClosingValue()-low;
		top*=multiplicator;
		g.drawLine(i, (int)(buttom), i, (int)(top));
	}
	File outputfile=new File(filename+".gif");
	try {
		ImageIO.write(bi, "gif", outputfile);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}

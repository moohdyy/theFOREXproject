package hneat;
import java.util.Random;


public class CandlestickTrial {
	private int horResolution = 50;
	private int vertResolution = 50;
	private Random rand = new Random();
	private int caseNo;
	
	public CandlestickTrial() {
		caseNo = rand.nextInt(2048)+1;
	}
	
	public int getHorResolution() {
		return this.horResolution;
	}
	public int getVertResolution() {
		return this.vertResolution;
	}
	public int getCaseNo() {
		return this.caseNo;
	}
}

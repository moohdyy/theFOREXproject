package Connection;

public class Candlestick {
	private Time timestamp;
	private double high;
	private double low;
	private double opening;
	private double closing;

	public Candlestick(Time t, double s, double e, double l, double h) {
		this.timestamp = t;
		this.opening = s;
		this.closing = e;
		this.low = l;
		this.high = h;
	}

	public Time getTimestamp() {
		return timestamp;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getOpening() {
		return opening;
	}

	public double getClosing() {
		return closing;
	}
}

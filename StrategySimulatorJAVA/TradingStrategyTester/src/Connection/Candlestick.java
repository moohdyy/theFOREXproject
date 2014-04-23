package Connection;

public class Candlestick {
	private Time timestamp;
	private double high;
	private double low;
	private double start;
	private double end;

	public Candlestick(Time t, double s, double e, double l, double h) {
		this.timestamp = t;
		this.start = s;
		this.end = e;
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

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}
}

package forexstrategies;


import Connection.Candlestick;
import Connection.Time;
import forexstrategies.JapaneseCandlesticksStrategy.Trend;

import java.util.ArrayList;

public class JapaneseCandlestick {
	private double openingValue;
	private double closingValue;
	private double lowestValue;
	private double lowerBody;
	private double upperBody;
	private double highestValue;
	private Time time;

	public enum Categories {
		None, Bullish, Bearish
	}

	public enum Colors {
		None, White, Black
	};

	public enum Types {
		None, SpinningTop, WhiteMarubozu, BlackMarubozu, LongLeggedDoji, DragonflyDoji, GravestoneDoji, FourPriceDoji, Hammer, InvertedHammer
	};

	public enum Patterns {
		None, Hammer, HangingMan, InvertedHammer, ShootingStar, BullishEngulfing, BearishEngulfing, TweezerTops, TweezerBottoms, EveningStar, MorningStar, ThreeWhiteSoldiers, ThreeBlackCrows, ThreeInsideUp, ThreeInsideDown
	};

	private Categories category = Categories.None;
	private Colors color = Colors.None;
	private Types type = Types.None;
	private Patterns pattern = Patterns.None;
	private final static int trend = 5;

	public JapaneseCandlestick(Candlestick c) {
		this.openingValue = c.getOpening();
		this.closingValue = c.getClosing();
		this.highestValue = c.getHigh();
		this.lowestValue = c.getLow();
		this.time = c.getTimestamp();
		if (openingValue > closingValue) {
			this.upperBody = openingValue;
			this.lowerBody = closingValue;
		} else {
			this.upperBody = closingValue;
			this.lowerBody = openingValue;
		}
		color = determineColor();
		type = determineType();
		category = determineCategory();
	}

	public Categories determineCategory() {
		if (type == Types.WhiteMarubozu) {
			return Categories.Bullish;
		} else if (type == Types.BlackMarubozu) {
			return Categories.Bearish;
		} else if (type == Types.SpinningTop) {
			return Categories.None;
		} else if (type == Types.DragonflyDoji) {
			return Categories.None;
		} else if (type == Types.FourPriceDoji) {
			return Categories.None;
		} else if (type == Types.LongLeggedDoji) {
			return Categories.None;
		} else if (type == Types.GravestoneDoji) {
			return Categories.None;
		} else if (color == Colors.Black) {
			return Categories.Bearish;
		} else if (color == Colors.White) {
			return Categories.Bullish;
		}
		return Categories.None;

	}

	public Categories getCategory() {
		return category;
	}

	public Colors determineColor() {
		if (closingValue > openingValue) {
			return Colors.White;
		} else if (closingValue < openingValue) {
			return Colors.Black;
		}
		return Colors.None;
	}

	public Types determineType() {
		if (color == Colors.None) {
			if (highestValue == lowestValue) {
				return Types.FourPriceDoji;
			} else if (lowestValue == openingValue) {
				return Types.GravestoneDoji;
			} else if (highestValue == openingValue) {
				return Types.DragonflyDoji;
			} else {
				return Types.LongLeggedDoji;
			}
		} else if ((highestValue == openingValue)
				&& (lowestValue == closingValue)) {
			return Types.BlackMarubozu;
		} else if ((highestValue == closingValue)
				&& (lowestValue == openingValue)) {
			return Types.WhiteMarubozu;
		}
		double topStick;
		double bottomStick;
		if (closingValue > openingValue) {
			topStick = highestValue - closingValue;
			bottomStick = openingValue - lowestValue;
		} else {
			topStick = highestValue - openingValue;
			bottomStick = closingValue - lowestValue;
		}
		double body = Math.abs(openingValue - closingValue);
		if (((body * 2) <= bottomStick) && ((body / 3.0) > topStick)) {
			return Types.Hammer;
		} else if (((body * 2) <= topStick) && ((body / 3.0) > bottomStick)) {
			return Types.InvertedHammer;
		}
		if ((body > topStick) && (body > bottomStick)
				&& body <= getSize() / 4.0) {
			return Types.SpinningTop;
		}
		return Types.None;
	}

	static double multiply = 0.05;

	public static Patterns determinePattern(
			ArrayList<JapaneseCandlestick> candlesticks, int index,
			Trend trend, Trend trend2) {
		if (index > 0) {
			if (candlesticks.get(index - 1).type == Types.Hammer) {

				if (candlesticks.get(index).getClosingValue() >= candlesticks
						.get(index - 1).getOpeningValue()) {

					if (candlesticks.get(index).getColor() == Colors.White) {
						return Patterns.Hammer;
					}

				} else if (candlesticks.get(index).getOpeningValue() <= candlesticks
						.get(index - 1).getClosingValue()) {
					if (candlesticks.get(index).getColor() == Colors.Black) {
						return Patterns.HangingMan;
					}

				}
			} else if (candlesticks.get(index).type == Types.InvertedHammer) {
				if (candlesticks.get(index).getClosingValue() >= candlesticks
						.get(index - 1).getOpeningValue()) {

					if (candlesticks.get(index).getColor() == Colors.White) {
						return Patterns.InvertedHammer;
					}

				} else if (candlesticks.get(index).getOpeningValue() <= candlesticks
						.get(index - 1).getClosingValue()) {
					if (candlesticks.get(index).getColor() == Colors.Black) {
						return Patterns.ShootingStar;
					}

				}
			}

			double dif = candlesticks.get(index).highestValue
					- candlesticks.get(index).lowestValue;
			// dif*=0.05;
			dif *= multiply;
			dif = Math.abs(dif);
			if (hasDowntrend(trend)) {
				if (candlesticks.get(index).color == Colors.White) {
					if (candlesticks.get(index - 1).color == Colors.Black) {
						if (Math.abs(candlesticks.get(index).getLowestValue()
								- candlesticks.get(index - 1).getLowestValue()) <= dif) {
							double shadowIndex = candlesticks.get(index).highestValue
									- candlesticks.get(index).upperBody;
							double shadowBefIndex = candlesticks.get(index - 1).highestValue
									- candlesticks.get(index - 1).upperBody;
							if (Math.abs(shadowBefIndex - shadowIndex) <= dif) {
								shadowIndex = candlesticks.get(index).lowerBody
										- candlesticks.get(index).lowestValue;
								shadowBefIndex = candlesticks.get(index - 1).lowerBody
										- candlesticks.get(index - 1).lowestValue;
								if (Math.abs(shadowBefIndex - shadowIndex) <= dif) {
									return Patterns.TweezerBottoms;
								}
							}
						}
						if (candlesticks.get(index - 1).getUpperBody() <= (candlesticks
								.get(index).getUpperBody() + dif)) {
							if (candlesticks.get(index - 1).getLowerBody() >= (candlesticks
									.get(index).getLowerBody() - dif)) {
								return Patterns.BullishEngulfing;
							}
						}
					}
				}
			} else if (hasUptrend(trend)) {
				if (candlesticks.get(index - 1).color == Colors.White) {
					if (candlesticks.get(index).color == Colors.Black) {
						if (Math.abs(candlesticks.get(index).getHighestValue()
								- candlesticks.get(index - 1).getHighestValue()) <= dif) {
							double shadowIndex = candlesticks.get(index).highestValue
									- candlesticks.get(index).upperBody;
							double shadowBefIndex = candlesticks.get(index - 1).highestValue
									- candlesticks.get(index - 1).upperBody;
							if (Math.abs(shadowBefIndex - shadowIndex) <= dif) {
								shadowIndex = candlesticks.get(index).lowerBody
										- candlesticks.get(index).lowestValue;
								shadowBefIndex = candlesticks.get(index - 1).lowerBody
										- candlesticks.get(index - 1).lowestValue;
								if (Math.abs(shadowBefIndex - shadowIndex) <= dif) {
									return Patterns.TweezerTops;
								}
							}
						}
						if (candlesticks.get(index - 1).upperBody <= (candlesticks
								.get(index).upperBody + dif)) {
							if (candlesticks.get(index - 1).lowerBody >= (candlesticks
									.get(index).lowerBody - dif)) {
								return Patterns.BearishEngulfing;
							}
						}
					}
				}

			}
			if(index>1)
			{
			if (hasDowntrend(trend)) {
				if (candlesticks.get(index - 2).getCategory() == Categories.Bearish
						|| candlesticks.get(index - 2).getType() == Types.Hammer) {
					if ((candlesticks.get(index - 1).getType() == Types.SpinningTop)
							|| (candlesticks.get(index - 1).getType() == Types.FourPriceDoji)
							|| (candlesticks.get(index - 1).getType() == Types.GravestoneDoji)
							|| (candlesticks.get(index - 1).getType() == Types.LongLeggedDoji)
							|| (candlesticks.get(index - 1).getType() == Types.DragonflyDoji)) {
						double midpoint = candlesticks.get(index - 2)
								.getHighestValue()
								- candlesticks.get(index - 2).getLowestValue();
						midpoint /= 2.0;
						midpoint += candlesticks.get(index - 2)
								.getLowestValue();
						if (midpoint < candlesticks.get(index).closingValue) {
							return Patterns.MorningStar;
						}
					}
				}
			} else if (hasUptrend(trend)) {
				if (candlesticks.get(index - 2).getCategory() == Categories.Bullish
						|| candlesticks.get(index - 2).getType() == Types.InvertedHammer) {
					if ((candlesticks.get(index - 1).getType() == Types.SpinningTop)
							|| (candlesticks.get(index - 1).getType() == Types.FourPriceDoji)
							|| (candlesticks.get(index - 1).getType() == Types.GravestoneDoji)
							|| (candlesticks.get(index - 1).getType() == Types.LongLeggedDoji)
							|| (candlesticks.get(index - 1).getType() == Types.DragonflyDoji)) {
						double midpoint = candlesticks.get(index - 2)
								.getHighestValue()
								- candlesticks.get(index - 2).getLowestValue();
						midpoint /= 2.0;
						midpoint += candlesticks.get(index - 2)
								.getLowestValue();
						if (midpoint > candlesticks.get(index).closingValue) {
							return Patterns.EveningStar;
						}
					}
				}
			}
			JapaneseCandlestick third = candlesticks.get(index);
			JapaneseCandlestick second = candlesticks.get(index - 1);
			JapaneseCandlestick first = candlesticks.get(index - 2);
			if (hasDowntrend(trend2)) {
				if (first.getCategory() == Categories.Bearish) {
					double midpoint = first.getHighestValue()
							- first.getLowestValue();
					midpoint /= 2.0;
					midpoint += first.getLowestValue();
					midpoint += candlesticks.get(index - 2).getLowestValue();
					if (second.getHighestValue() > midpoint) {
						if (third.getClosingValue() > first.getHighestValue()) {
							return Patterns.ThreeInsideUp;
						}
					}

				}
			} else if (hasUptrend(trend2)) {
				if (first.getCategory() == Categories.Bullish) {
					double midpoint = first.getHighestValue()
							- first.getLowestValue();
					midpoint /= 2.0;
					midpoint += first.getLowestValue();
					if (midpoint > second.getLowestValue()) {
						if (third.getClosingValue() < first.getLowestValue()) {
							return Patterns.ThreeInsideDown;
						}
					}

				}
			}

			if (hasDowntrend(trend2)) {
				if ((third.color == Colors.White)
						&& (second.color == Colors.White)
						&& (first.color == Colors.White)) {
					if ((first.getBodySize() < second.getSize())
							&& (second.hasSmallUpperShadow())) {
						if (second.getBodySize() <= third.getSize()) {
							if (third.hasSmallUpperShadow()) {
								return Patterns.ThreeWhiteSoldiers;
							}
						}
					}
				}
			} else if (hasUptrend(trend2)) {
				if ((third.color == Colors.Black)
						&& (second.color == Colors.Black)
						&& (first.color == Colors.Black)) {
					if ((first.getBodySize() < second.getSize())
							&& (second.smallLowerShadow())) {
						if (second.getBodySize() <= third.getSize()) {
							if (third.smallLowerShadow()) {
								return Patterns.ThreeWhiteSoldiers;
							}
						}
					}
				}
			}
		}
		}
		return Patterns.None;
	}

	public double getSize() {
		return this.highestValue - this.lowestValue;
	}

	public double getBodySize() {
		return (upperBody - lowerBody);
	}

	public Time getTime() {
		return this.time;
	}

	private boolean hasSmallUpperShadow(double percentage) {
		double upperShadow = highestValue - upperBody;
		double sizeBodyP = upperBody - lowerBody;
		sizeBodyP *= percentage;
		if (upperShadow < sizeBodyP) {
			return true;
		}
		return false;
	}

	private boolean hasSmallLowerShadow(double percentage) {
		double lowerShadow = lowerBody - lowestValue;
		double sizeBodyP = upperBody - lowerBody;
		sizeBodyP *= percentage;
		if (lowerShadow < sizeBodyP) {
			return true;
		}
		return false;
	}

	public boolean hasSmallUpperShadow() {
		return hasSmallUpperShadow((1.0 / 3.0));
	}

	public boolean smallLowerShadow() {
		return hasSmallLowerShadow((1.0 / 3.0));
	}

	public boolean hasNoUpperShadow() {
		return hasSmallUpperShadow(0.001);
	}

	public boolean hasNoLowerShadow() {
		return hasSmallLowerShadow(0.001);
	}

	public double getSizeUpperShadow() {
		return (highestValue - upperBody);
	}

	public double getSizeLowerShadow() {
		return (lowerBody - lowestValue);
	}

	public static boolean hasDowntrend(Trend trend) {
		if (trend == Trend.falling) {
			return true;
		}
		return false;
	}

	public static boolean hasUptrend(Trend trend) {
		if (trend == Trend.rising) {
			return true;
		}
		return false;
	}

	public static boolean buyingSignal(Patterns pattern) {
		if (pattern == Patterns.BullishEngulfing) {
			return true;
		} else if (pattern == Patterns.TweezerBottoms) {
			return true;
		} else if (pattern == Patterns.MorningStar) {
			return true;
		} else if (pattern == Patterns.ThreeWhiteSoldiers) {
			return true;
		} else if (pattern == Patterns.ThreeInsideUp) {
			return true;
		} else if (pattern == Patterns.Hammer) {
			return true;
		} else if (pattern == Patterns.InvertedHammer) {
			return true;
		}
		return false;
	}

	public static boolean sellingSignal(Patterns pattern) {

		if (pattern == Patterns.BearishEngulfing) {
			return true;
		} else if (pattern == Patterns.TweezerTops) {
			return true;
		} else if (pattern == Patterns.EveningStar) {
			return true;
		} else if (pattern == Patterns.ThreeBlackCrows) {
			return true;
		} else if (pattern == Patterns.ThreeInsideDown) {
			return true;
		} else if (pattern == Patterns.HangingMan) {
			return true;
		} else if (pattern == Patterns.ShootingStar) {
			return true;
		}
		return false;
	}

	public double getOpeningValue() {
		return openingValue;
	}

	public double getClosingValue() {
		return closingValue;
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

	public Colors getColor() {
		return color;
	}

	public Types getType() {
		return type;
	}

	public Patterns getPattern() {
		return pattern;
	}
}

package Connection;

public class Time {
	int day;
	int month;
	int year;
	int hours;
	int minutes;
	int seconds;

	public Time(String date, String time) {
		String[] d = date.split(".");
		if (d.length == 3) {
			year = Integer.parseInt(d[0]);
			month = Integer.parseInt(d[1]);
			day = Integer.parseInt(d[2]);
		}
		String[] t = time.split(":");
		if (t.length == 2) {
			hours = Integer.parseInt(t[0]);
			minutes = Integer.parseInt(t[1]);
		}

	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public boolean same(Time object) {
		if (seconds == object.seconds) {
			if (minutes == object.minutes) {

				if (hours == object.hours) {
					if (day == object.day) {
						if (month == object.month) {

							if (year == object.year) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}

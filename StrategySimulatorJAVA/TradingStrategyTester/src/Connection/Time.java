package Connection;

public class Time {
	int day;
	int month;
	int year;
	int hours;
	int minutes;
	int seconds;
public Time(String t)
{
	
}
public int getDay()
{
	return day;
}
public int getMonth()
{
	return month;
}
public int getYear()
{
	return year;
}
public int getHours()
{
	return hours;
}
public int getMinutes()
{
	return minutes;
}
public int getSeconds()
{
	return seconds;
}
public boolean same(Time object)
{
	if(seconds==object.seconds)
	{
		if(minutes==object.minutes)
		{
			
		if(hours==object.hours)
		{
	if(day==object.day)
	{
	if(month==object.month)
	{
		
		if(year==object.year)
		{
			return true;
		}
		}
	}
		}}}
	return false;
}
}

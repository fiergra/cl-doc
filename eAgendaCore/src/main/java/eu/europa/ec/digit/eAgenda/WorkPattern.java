package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkPattern implements Serializable {
	private static final long serialVersionUID = -5577328425239811358L;
	
	public IResource resource;
	public Room location;

	public Date from;
	public Date until;
	public int minuteGrid = 15;
	
//	public enum WeekDay {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
	public List<Day> days;

	
	public WorkPattern() {}
	
	public void setDay(int i, Day day) {
		if (days == null) {
			days = new ArrayList<>(7);
			for (int d = 0; d < 7; d++) { days.add(new Day());}
		}
		days.add(i, day);
	}
	
	public Day getDay(int i) {
		Day day = days == null ? null : days.get(i);
		
		if (day == null) {
			day = new Day();
			setDay(i, day);
		}
		
		return day;
	}

	public boolean applies(Date d) {
		return (from == null || from.getTime() <= d.getTime()) && (until == null || until.getTime() >= d.getTime()) ;
	}

	public Day getDay(Date d) {
		@SuppressWarnings("deprecation")
		int dDay = d.getDay() - 1;
		if (dDay == -1) { // sunday overflow 
			dDay = 6;
		}
		Day day = getDay(dDay);
		return day;
	}
	
}

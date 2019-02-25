package com.ceres.dynamicforms.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class ClientDateHelper {

	public static final long MINUTE_MS = 60 * 1000;
	public static final long HOUR_MS = 60 * MINUTE_MS;
	public static final long DAY_MS = 24 * HOUR_MS;
	
	private static DateTimeFormat hf = DateTimeFormat.getFormat("HH");
	public static DateTimeFormat tf = DateTimeFormat.getFormat("HH:mm");
	public static DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy");
	public static DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

	@SuppressWarnings("deprecation")
	public static Date addMonths(Date date, int months) {
		Date newDate = new Date(date.getTime());
		newDate.setMonth(date.getMonth() + months);
		return newDate;
	}

	@SuppressWarnings("deprecation")
	public static int diffInMonths(Date date1, Date date2) {
		return (date1.getYear() * 12 + date1.getMonth()) - (date2.getYear() * 12 + date2.getMonth());
	}

	public static int diffInDays(Date date1, Date date2) {
		long l1 = trunc(date1).getTime();
		long l2 = trunc(date2).getTime();
		long diff = Math.abs(l1 - l2);
		
		return new Long(diff / DAY_MS).intValue();
	}

	@SuppressWarnings("deprecation")
	public static Date addMinutes(Date date, int duration) {
		Date newDate = new Date(date.getTime());
		newDate.setMinutes(date.getMinutes() + duration);
		return newDate;
	}

	public static Date addDays(Date date, int days) {
		long lDays = days;
		Date newDate = new Date(date.getTime() + lDays * 24 * 60 * 60 * 1000);
		return newDate;
	}
	
	public static Date addWorkingDays(Date date, int workingDays) {
		Date newDate = date;
		for(int i=0;i<=workingDays;i++ ) {
			newDate = addDays(newDate, 1);
			if(getDayOfWeek(newDate) == 6) {//Saturday
				//add the rest of the business days to Monday
				newDate = addDays(newDate, 2);
			} else if(getDayOfWeek(newDate) == 0) {//Sunday
				//add the rest of the days to Monday
				newDate = addDays(newDate, 1);
			}
		}
		
		return newDate;
		
	}

	private static DateTimeFormat dateOnly = DateTimeFormat.getFormat("dd/MM/yyyy");

	public static Date trunc(Date date) {
		if (date != null) {
			String sDate = dateOnly.format(date);
			Date newDate = dateOnly.parse(sDate);
			return newDate;
		} else {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static void setSeconds(Date date, int seconds) {
		date.setSeconds(seconds);
	}

	@SuppressWarnings("deprecation")
	public static int getMinutes(Date date) {
		return date.getMinutes();
	}

	public static Date setTime(Date date, int hours, int minutes) {
		String dateOnly = df.format(date);
		Date withTime = dtf.parse(dateOnly + " " + hours + ":" + minutes);
		return withTime;
	}
	
	@SuppressWarnings("deprecation")
	public static int getDayOfMonth(Date date) {
		return date.getDate();
	}

	@SuppressWarnings("deprecation")
	public static int getDayOfWeek(Date date) {
		return date.getDay();
	}

	@SuppressWarnings("deprecation")
	public static int getDate(Date date) {
		return date.getDate();
	}

	@SuppressWarnings("deprecation")
	public static void setDate(Date date, int dateToSet) {
		date.setDate(dateToSet);
	}

	@SuppressWarnings("deprecation")
	public static int getMonth(Date date) {
		return date.getMonth();
	}

	@SuppressWarnings("deprecation")
	public static void setMonth(Date date, int month) {
		date.setMonth(month);
	}
	
	public static int getHours(Date date) {
		String sH = hf.format(date);
		return Integer.valueOf(sH);
	}

	@SuppressWarnings("deprecation")
	public static int getYear(Date date) {
		return date.getYear();
	}
	
	@SuppressWarnings("deprecation")
	public static void setYear(Date date, int year) {
		date.setYear(year);
	}

	@SuppressWarnings("deprecation")
	public static boolean sameYear(Date date1, Date date2) {
		return date1.getYear() == date2.getYear();
	}

	@SuppressWarnings("deprecation")
	public static boolean sameMonth(Date date1, Date date2) {
		return date1.getMonth() == date2.getMonth();
	}

	public static boolean sameDay(Date date1, Date date2) {
		return ClientDateHelper.trunc(date1).equals(ClientDateHelper.trunc(date2));
	}


}

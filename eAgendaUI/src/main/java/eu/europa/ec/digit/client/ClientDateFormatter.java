package eu.europa.ec.digit.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

@SuppressWarnings("deprecation")
public class ClientDateFormatter {
	
	
	private static boolean sameYear(Date dateFrom, Date dateTo) {
		return dateFrom.getYear() == dateTo.getYear();
	}

	private static boolean sameMonthYear(Date dateFrom, Date dateTo) {
		return dateFrom.getMonth() == dateTo.getMonth() && sameYear(dateFrom, dateTo);
	}

	private static boolean sameDayMonthYear(Date dateFrom, Date dateTo) {
		return dateFrom.getDay() == dateTo.getDay() && sameMonthYear(dateFrom, dateTo);
	}

	public static DateTimeFormat dtfDay = DateTimeFormat.getFormat("dd");
	public static DateTimeFormat dtfMonth = DateTimeFormat.getFormat("dd/MM");
	public static DateTimeFormat dtfYear = DateTimeFormat.getFormat("dd/MM/yyyy");
	public static DateTimeFormat dtfDayTime = DateTimeFormat.getFormat("dd/MM HH:mm");
	public static DateTimeFormat dtfTime = DateTimeFormat.getFormat("HH:mm");

	public static NumberFormat cnf = NumberFormat.getSimpleCurrencyFormat("EUR");
	public static NumberFormat dnf = NumberFormat.getDecimalFormat();
	
	public static String format(Date dateFrom) {
		Date now = new Date();
		String dates;
		
		if (sameDayMonthYear(dateFrom, now)) {
			dates = "today";
		} else if (sameMonthYear(dateFrom, now)) {
			dates = dtfDay.format(dateFrom);
		} else if (sameYear(dateFrom, now)) {
			dates = dtfMonth.format(dateFrom);
		} else {
			dates = dtfYear.format(dateFrom);
		}
		return dates;
	}

	public static String format(Date dateFrom, Date dateTo) {
		String dates;
		
		if (sameDayMonthYear(dateFrom, dateTo)) {
			dates = dtfYear.format(dateFrom);
		} else if (sameMonthYear(dateFrom, dateTo)) {
			dates = dtfDay.format(dateFrom) + "-" + dtfYear.format(dateTo);
		} else if (sameYear(dateFrom, dateTo)) {
			dates = dtfMonth.format(dateFrom) + "-" + dtfYear.format(dateTo);
		} else {
			dates = dtfYear.format(dateFrom) + "-" + dtfYear.format(dateTo);
		}
		return dates;
	}

	public static String getStartEnd(Date dateFrom, Date dateTo) {
		return dtfDayTime.format(dateFrom) + " - " + dtfDayTime.format(dateTo);
	}
}

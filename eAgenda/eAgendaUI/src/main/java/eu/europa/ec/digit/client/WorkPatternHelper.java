package eu.europa.ec.digit.client;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.ceres.dynamicforms.client.ClientDateHelper;

import eu.europa.ec.digit.eAgenda.Appointment;
import eu.europa.ec.digit.eAgenda.Slot;
import eu.europa.ec.digit.eAgenda.WorkPattern;

public class WorkPatternHelper {

	private Date minDate = null;
	private Date maxDate = null;
	private List<WorkPattern>patterns = null;

	public WorkPatternHelper() {}
	
	public void setPatterns(int delayInH, List<WorkPattern> patterns) {
		this.patterns = patterns;
		updateMinMaxDates(delayInH);
	}
	
	public WorkPattern getPatternForDay(Date d) {
		WorkPattern wp = null;
		Iterator<WorkPattern> i = patterns.iterator();
		
		while (i.hasNext() && wp == null) {
			WorkPattern curr = i.next();
			
			if (curr.applies(d)) {
				wp = curr;
			}
		}
			
		return wp;
	}

	public Date getPreferredDate(Date curr) {
		Date preferredDate = curr;
		Date now = ClientDateHelper.trunc(new Date());
		
		if (curr == null) {
			preferredDate = getMinDate();
		} else {
			if (getMinDate() != null && curr.getTime() < getMinDate().getTime()) {
				preferredDate = getMinDate();
			} else if (maxDate != null && curr.getTime() > maxDate.getTime()) {
				preferredDate = maxDate;
			}
		}
		
		return preferredDate == null || preferredDate.getTime() < now.getTime() ? now : preferredDate;
	}

	private void updateMinMaxDates(int delayInH) {
		minDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
		maxDate = new Date(0);
		
		patterns.forEach(p -> {
			if (p.getFrom() == null || getMinDate() == null || getMinDate().getTime() > p.getFrom().getTime()) {
				minDate = p.getFrom();
			}
			
			if (p.until == null || maxDate == null || maxDate.getTime() < p.until.getTime()) {
				maxDate = p.until;
			}
		});
		
		Date minPlusDelay = new Date(new Date().getTime() + delayInH * 60L * 60L * 1000L);
		if (minDate == null || minDate.getTime() <= minPlusDelay.getTime()) {
			minDate = minPlusDelay;
		}
		
	}

	public Date getMinDate() {
		return minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public List<Appointment> getAppointmentsInSlot(List<Appointment> appointments, Date date, Slot slot) {
		return appointments.parallelStream().filter(a -> inSlot(a, date, slot)).collect(Collectors.toList());		
	}
	
	private boolean inSlot(Appointment a, Date d, Slot slot) {
		long aStart = a.from.getTime();
		long aEnd = a.until.getTime();
		
		long sStart = slot.getFrom(d).getTime();
		long sEnd = slot.getUntil(d).getTime();
		
		return (aStart <= sStart && aEnd >= sEnd) || (aStart >= sStart && aStart < sEnd)  || (aEnd > sStart && aEnd <= sEnd);
	}

	
	
	
}

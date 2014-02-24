package com.ceres.cldoc.timemanagement;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ceres.cldoc.model.Act;

public class TimeSheetDay extends SimpleTimeSheetElement {
	private static final long serialVersionUID = -7687397537201248342L;
	private boolean isHoliday;

	public TimeSheetDay() {}

	public TimeSheetDay(Date date, boolean isHoliday, int quota) {
		super(date, quota);
		this.isHoliday = isHoliday;
	}
	

	
	public void add(Act act) {
		addChild(new ActAsTimeSheetElement(act));
	}

	@Override
	public String toString() {
		return (isHoliday ? "*DAY" : " DAY: ") + getDate() + ": " + super.toString();
	}

	@Override
	public int getAbsences() {
		return isHoliday ? 0 : super.getAbsences();
	}

	@Override
	public int getQuota() {
		return isAbsent() ? 0 : super.getQuota();
	}


	public boolean isHoliday() {
		return isHoliday;
	}

	public void setActs(List<Map<String, Serializable>> acts) {
		clearChildren();
		for (Map<String, Serializable> act:acts) {
			add((Act) act);
		}
	}

	@Override
	public Date getDate() {
		Date d = super.getDate();
		d.setHours(0);
		d.setMinutes(0);
		d.setSeconds(0);
		
		return d;
	}


}

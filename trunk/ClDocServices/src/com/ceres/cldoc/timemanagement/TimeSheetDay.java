package com.ceres.cldoc.timemanagement;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;

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

}

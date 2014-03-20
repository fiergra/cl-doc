package com.ceres.cldoc.timemanagement;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;

public class TimeSheetDay extends SimpleTimeSheetElement {
	private static final long serialVersionUID = -7687397537201248342L;
	private boolean isPublicHoliday;
	private int quota = 0;

	public TimeSheetDay() {
	}

	public TimeSheetDay(TimeSheetMonth tsm) {
		super(tsm);
	}

	public TimeSheetDay(TimeSheetMonth tsm, Date date, boolean isPublicHoliday, int quota) {
		super(tsm, date);
		this.isPublicHoliday = isPublicHoliday;
		this.quota = quota;
	}
	
	@Override
	public int getQuota() {
		return isAbsent() ? 0 : (quota + super.getQuota());
	}


	public void add(Act act) {
		addChild(new ActAsTimeSheetElement(act));
	}

	@Override
	public String toString() {
		return (isPublicHoliday ? "*DAY" : " DAY: ") + getDate() + ": " + super.toString();
	}

	@Override
	public int getAnnualLeaveDays() {
		return (isPublicHoliday || quota == 0) ? 0 : super.getAnnualLeaveDays();
	}

	public boolean isPublicHoliday() {
		return isPublicHoliday;
	}

	public void setActs(List<Act> acts) {
		clearChildren();
		for (Act act:acts) {
			add(act);
		}
		notifyParent();
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

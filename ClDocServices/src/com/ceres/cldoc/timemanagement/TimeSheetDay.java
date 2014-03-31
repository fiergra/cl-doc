package com.ceres.cldoc.timemanagement;

import java.util.Date;
import java.util.List;

import sun.security.action.GetLongAction;

import com.ceres.cldoc.model.Act;

public class TimeSheetDay extends SimpleTimeSheetElement {
	private static final long serialVersionUID = -7687397537201248342L;

	private static final int MAX_DAILY = 10 * 60;

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
		int q;
//		Date now = new Date();
//		return (isAbsent() || getDate().getTime() >= now.getTime() ) ? 0 : (quota + super.getQuota());
		Date now = new Date();
		if (isAbsent()) {
			int sq = quota + super.getQuota();
			q = new Float(sq - getAbsenceDays() * sq).intValue();
		} else if (getDate().getTime() >= now.getTime()) {
			q = 0;
		} else {
			q = quota + super.getQuota();
		}
		return q;
	}


	public void add(Act act) {
		addChild(new ActAsTimeSheetElement(act));
	}

	@Override
	public String toString() {
		return (isPublicHoliday ? "*DAY" : " DAY: ") + getDate() + ": " + super.toString();
	}

	@Override
	public float getAnnualLeaveDays() {
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

	@Override
	public int getWorkingTime() {
		int workingTime = super.getWorkingTime();
		return workingTime > MAX_DAILY ? MAX_DAILY : workingTime;
	}

	
	

}

package com.ceres.cldoc.timemanagement;

import java.util.Date;

public class TimeSheetMonth extends SimpleTimeSheetElement {

	private static final long serialVersionUID = 7022512742466220875L;
	private WorkPattern wp;

	public TimeSheetMonth() {
	}

	public TimeSheetMonth(TimeSheetYear tsy) {
		super(tsy);
	}

	public TimeSheetMonth(TimeSheetYear tsy, WorkPattern wp, Date month) {
		super(tsy, month);
		this.wp = wp;
	}

	@Override
	public String toString() {
		return "MONTH: " + getDate() + ": " + super.toString();
	}

	public WorkPattern getWp() {
		return wp;
	}

	public float getLeaveEntitlement(int baseEntitlement) {
		float workigDaysPerWeek = 0f;
		for (float f : getWp().hours) {
			if (f > 0f) {
				workigDaysPerWeek++;
			}
		}
		return (workigDaysPerWeek / 5 * baseEntitlement) / 12;
	}
	
	
}

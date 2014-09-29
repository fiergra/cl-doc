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

	/* http://www.arbeitsrecht.org/arbeitnehmer/urlaub/so-berechnen-sie-ihren-urlaubsanspruch/ */
	
	public float getLeaveEntitlement(float baseEntitlement) {
		if (getWp() != null) {
			float workigDaysPerWeek = 0f;
			for (float f : getWp().hours) {
				if (f > 0f) {
					workigDaysPerWeek++;
				}
			}
			float entitlement = (workigDaysPerWeek / 5 * baseEntitlement) / 12; 
			return entitlement;
		} else {
			return 0f;
		}
	}
	
	
}

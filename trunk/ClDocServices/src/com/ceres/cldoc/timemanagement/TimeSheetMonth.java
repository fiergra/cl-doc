package com.ceres.cldoc.timemanagement;

import java.util.Date;

public class TimeSheetMonth extends SimpleTimeSheetElement {

	private static final long serialVersionUID = 7022512742466220875L;

	public TimeSheetMonth() {}

	public TimeSheetMonth(Date month, int quota) {
		super(month, quota);
	}

	@Override
	public String toString() {
		return "MONTH: " + getDate() + ": " + super.toString();
	}

	
	
}

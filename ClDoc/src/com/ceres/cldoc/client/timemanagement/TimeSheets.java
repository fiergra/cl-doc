package com.ceres.cldoc.client.timemanagement;

import java.util.Date;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class TimeSheets extends TabLayoutPanel {

	public TimeSheets(ClDoc clDoc) {
		super(2.5, Unit.EM);
		Date now = new Date();
		int currentYear = now.getYear() + 1900;
		int currentMonth = now.getMonth();
		
		if (currentMonth > 5) {
			add(new TimeSheet(clDoc, currentYear), String.valueOf(currentYear));
			add(new TimeSheet(clDoc, currentYear + 1), String.valueOf(currentYear + 1));
		} else {
			add(new TimeSheet(clDoc, currentYear - 1), String.valueOf(currentYear - 1));
			add(new TimeSheet(clDoc, currentYear), String.valueOf(currentYear));
		}
	}

}

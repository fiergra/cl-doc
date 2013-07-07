package com.ceres.cldoc.client.controls;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;


public class TimeTextBox extends DateTextBox {
	
	@Override
	protected DateTimeFormat[] getDateTimeFormats() {
		return new DateTimeFormat[] {
				DateTimeFormat.getFormat("HH:mm"),
				DateTimeFormat.getFormat("HHmm"),
				DateTimeFormat.getFormat("HH"),
				DateTimeFormat.getFormat("hmm")
			};	
	}
	
	@Override
	protected String formatValue(Date value) {
		return DateTimeFormat.getFormat("HH:mm").format(value);
	}



}

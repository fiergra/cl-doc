package com.ceres.dynamicforms.client.components;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;


public class TimeTextBox extends DateTextBox {
	
	private Date datePart;

	public TimeTextBox() {
		super();
		setWidth("3em");
	}

	@Override
	protected DateTimeFormat[] getDateTimeFormats() {
		return new DateTimeFormat[] {
				DateTimeFormat.getFormat("HH:mm"),
				DateTimeFormat.getFormat("HHmm"),
				DateTimeFormat.getFormat("HH"),
				DateTimeFormat.getFormat("hmm")
			};	
	}
	
	private boolean isTimeSet(Date date) {
		if (date != null) {
			String sDate = DateTimeFormat.getFormat("kkHmmss").format(date);
			return !sDate.equals("2400000");
		} else {
			return false;
		}
	}
	
	@Override
	public void setDate(Date value) {
		datePart = value;
		super.setDate(isTimeSet(value) ? value : null);
//		super.setDate(value);
	}


	@Override
	protected String formatValue(Date value) {
		return isTimeSet(value) ? DateTimeFormat.getFormat("HH:mm").format(value) : null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Date parseValue() {
		Date value = super.parseValue();
		if (datePart != null && value != null) {
			value.setDate(datePart.getDate());
			value.setMonth(datePart.getMonth());
			value.setYear(datePart.getYear());
		}
		return value;
	}



}

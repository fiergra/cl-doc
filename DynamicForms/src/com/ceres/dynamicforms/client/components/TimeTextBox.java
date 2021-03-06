package com.ceres.dynamicforms.client.components;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;


public class TimeTextBox extends DateTextBox {
	
	private Date datePart;

	public TimeTextBox() {
		super();
		getTextBox().setWidth("3em");
	}

	private static DateTimeFormat dtfTime24min = DateTimeFormat.getFormat("HHmm");
	
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
	
	public int getTime() {
		return getDate() != null ? Integer.parseInt(dtfTime24min.format(getDate())) : 0;
	}
	
	@Override
	public void setDate(Date value) {
		datePart = value;
		super.setDate(isTimeSet(value) ? value : null);
	}


	public void setDatePart(Date datePart) {
		this.datePart = datePart;
		setDate(parseValue());
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
			value.setYear(datePart.getYear());
			value.setMonth(datePart.getMonth());
			value.setDate(datePart.getDate());
		}
		return value;
	}



}

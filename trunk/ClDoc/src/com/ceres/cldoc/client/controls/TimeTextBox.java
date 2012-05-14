package com.ceres.cldoc.client.controls;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class TimeTextBox extends TextBox {

	private Time time;
	
	public TimeTextBox() {
		super();
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Time value = parseValue();
				
				if (value == null) {
					addStyleName("invalid");
				} else {
					removeStyleName("invalid");
					setTime(value);
				}
			}
		});
	}

	public Time parseValue() {
		String sValue = getValue();
		Time time = null;
		int index = sValue.indexOf(':');
		
		if (index != -1) {
			Integer hours = Integer.valueOf(sValue.substring(0, index));
			Integer minutes = Integer.valueOf(sValue.substring(index + 1));
			
			if (hours != null && minutes != null) {
				time = new Time(hours, minutes);
			}
		}		
		
		return time;
	}
	
	public void setTime(Time time) {
		this.time = time;
	}

	public Time getTime() {
		return time;
	}

	public Date getDate() {
		Date d = new Date();
		d.setHours(time.hours);
		d.setMinutes(time.minutes);
		return d;
	}

	public void setTime(Date dateValue) {
		setTime(new Time(dateValue));
	}
	
	
}

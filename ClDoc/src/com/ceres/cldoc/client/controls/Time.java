package com.ceres.cldoc.client.controls;

import java.io.Serializable;
import java.util.Date;

public class Time implements Serializable {
	private static final long serialVersionUID = -8065096930914616754L;
	public int hours;
	public int minutes;
	public int seconds;
	
	public Time() {
		super();
	}
	
	public Time(int hours, int minutes) {
		super();
		this.hours = hours;
		this.minutes = minutes;
	}

	public Time(Date dateValue) {
		this(dateValue.getHours(), dateValue.getMinutes());
	}
	
	
}

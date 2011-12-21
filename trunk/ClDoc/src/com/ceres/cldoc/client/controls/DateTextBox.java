package com.ceres.cldoc.client.controls;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.TextBox;

public class DateTextBox extends TextBox {

	private Date dateValue = null;
	
	public DateTextBox() {
		super();
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Date value = parseValue();
				
				if (value == null) {
					addStyleName("invalid");
				} else {
					removeStyleName("invalid");
					setDate(value);
				}
			}
		});
	}

	DateTimeFormat[] dateFormats = new DateTimeFormat[] {
		DateTimeFormat.getFormat("dd.MM.yyyy"),
		DateTimeFormat.getFormat("dd/MM/yyyy"),
		DateTimeFormat.getFormat("ddMMyyyy"),
		DateTimeFormat.getFormat("ddMMyy")
	};

	public Date parseValue() {
		String sValue = getValue();
		Date date = null;
		int i = 0;
		
		while (date == null && i < dateFormats.length) {
			try {
				date = dateFormats[i++].parseStrict(sValue);
			} catch (IllegalArgumentException x) {
				
			}
		}		
		return date;
	}
	
	public Date getDate() {
		return dateValue;
	}

	public void setDate(Date value) {
		dateValue = value;
		if (value != null) {
			setValue(DateTimeFormat.getFormat("dd.MM.yyyy").format(value));
		} else {
			setValue(null);
		}
	}
	
	
	
}

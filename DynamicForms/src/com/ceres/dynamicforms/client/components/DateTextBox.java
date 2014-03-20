package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
				}

				if (value != dateValue || (value != null && !value.equals(dateValue))) {
					setDate(value);
					notifyDateChangeHandlers(value);
				}
			}
		});
	}

	protected void notifyDateChangeHandlers(Date value) {
		for (ValueChangeHandler<Date> h:dateChangeHandlers) {
			h.onValueChange(null);
		}
	}

	private final DateTimeFormat[] dateFormats = getDateTimeFormats();

	@SuppressWarnings("deprecation")
	public Date parseValue() {
		String sValue = getValue();
		Date date = null;
		int i = 0;
		
		while (date == null && i < dateFormats.length) {
			try {
				date = dateFormats[i++].parseStrict(sValue);
//				if (date.getYear() < 100) {
//					date.setYear(date.getYear() + 1900);
//				}
			} catch (IllegalArgumentException x) {
				
			}
		}		
		return date;
	}
	
	protected DateTimeFormat[] getDateTimeFormats() {
		return new DateTimeFormat[] {
				DateTimeFormat.getFormat("dd.MM.yyyy"),
				DateTimeFormat.getFormat("dd/MM/yyyy"),
				DateTimeFormat.getFormat("ddMMyyyy"),
				DateTimeFormat.getFormat("dd.MM.yy"),
				DateTimeFormat.getFormat("ddMMyy"),
				DateTimeFormat.getFormat("dd.MM."),
				DateTimeFormat.getFormat("dd.MM"),
				DateTimeFormat.getFormat("dd/MM"),
				DateTimeFormat.getFormat("ddMM")
			};	
	}

	public Date getDate() {
		return dateValue;
	}

	public void setDate(Date value) {
		dateValue = value;
		if (value != null) {
			setValue(formatValue(value));
		} else {
			setValue(null);
		}
	}

	protected String formatValue(Date value) {
		return DateTimeFormat.getFormat("dd.MM.yyyy").format(value);
	}

	private final Collection<ValueChangeHandler<Date>> dateChangeHandlers = new ArrayList<ValueChangeHandler<Date>>();
	
	public void addDateChangeHandler(ValueChangeHandler<Date> dateChangeHandler) {
		dateChangeHandlers.add(dateChangeHandler);
	}
	
	
	
}

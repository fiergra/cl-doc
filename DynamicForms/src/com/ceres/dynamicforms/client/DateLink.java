package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.DateTextBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class DateLink extends InteractorWidgetLink<Map<String, Serializable>> {

	public DateLink(Interactor interactor, String fieldName, DateTextBox widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
		widget.addDateChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				interactor.onChange(DateLink.this);
			}
		});
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		Date date = (Date) get(item, name);
		
		if (date == null && getDefault() != null) {
			date = getDefaultDate();
		}
		getWidget().setDate(date);
	}

	private Date getDefaultDate() {
		switch (getDefault()) {
		case "NOW":
		case "TODAY":
			return new Date();
		default:
			logger.warning("unsupported date default: " + getDefault());
			return new Date();
		}
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getDate());
	}

	@Override
	public DateTextBox getWidget() {
		return (DateTextBox) super.getWidget();
	}

	@Override
	public boolean isEmpty() {
		return getWidget().getDate() == null;
	}
	
	

}

package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.DateTextBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class DateLink extends InteractorWidgetLink {

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
		getWidget().setDate((Date) get(item, name));
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

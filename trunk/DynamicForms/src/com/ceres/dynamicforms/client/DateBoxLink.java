package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

public class DateBoxLink extends InteractorWidgetLink {

	public DateBoxLink(final Interactor interactor, String fieldName,
			DateBox db, HashMap<String, String> attributes) {
		super(interactor, fieldName, db, attributes);
		db.setFireNullValues(true);
		final DateTimeFormat format = DateTimeFormat.getFormat("dd.MM.yyyy");
		db.setFormat(new DefaultFormat(format));
		db.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				interactor.onChange(DateBoxLink.this);
			}
		});

	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setValue((Date) get(item, name));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getValue());
	}

	
	@Override
	public DateBox getWidget() {
		return (DateBox) super.getWidget();
	}

	@Override
	public boolean isEmpty() {
		return getWidget().getValue() == null;
	}


}

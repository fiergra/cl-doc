package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.DateTextBox;

public class DateLink extends TextLink {

	public DateLink(Interactor interactor, String fieldName,
			DateTextBox widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setDate((Date) get(item, fieldName));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, fieldName, getWidget().getDate());
	}

	@Override
	protected DateTextBox getWidget() {
		return (DateTextBox) super.getWidget();
	}


}

package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.ceres.dynamicforms.client.components.DateTextBox;

public class DateFromLink extends DateLink {

	public DateFromLink(Interactor interactor, String fieldName,
			DateTextBox widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(INamedValues item) {
		getWidget().setDate(item.getDate());
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setDate(getWidget().getDate());
	}

}

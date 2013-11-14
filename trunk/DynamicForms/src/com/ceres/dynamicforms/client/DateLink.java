package com.ceres.dynamicforms.client;

import java.util.Date;
import java.util.HashMap;

import com.ceres.dynamicforms.client.components.DateTextBox;

public class DateLink extends TextLink {

	public DateLink(Interactor interactor, String fieldName,
			DateTextBox widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(INamedValues item) {
		getWidget().setDate((Date) item.getValue(fieldName));
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setValue(fieldName, getWidget().getDate());
	}

	@Override
	protected DateTextBox getWidget() {
		return (DateTextBox) super.getWidget();
	}


}

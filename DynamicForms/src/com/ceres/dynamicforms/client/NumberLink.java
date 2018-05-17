package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.NumberTextBox;

public class NumberLink extends TextLink {

	public NumberLink(Interactor interactor, String fieldName, NumberTextBox widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setNumber((Number) get(item, name));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getNumber());
	}

	@Override
	public NumberTextBox getWidget() {
		return (NumberTextBox) super.getWidget();
	}

	@Override
	public boolean isValid() {
		return !isEmpty() ? (getWidget().getNumber() != null) : super.isValid();
	}

	
	
}

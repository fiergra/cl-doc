package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.LongTextBox;

public class LongLink extends TextLink {

	public LongLink(Interactor interactor, String fieldName, LongTextBox widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setLong((Long) get(item, name));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getLong());
	}

	@Override
	public LongTextBox getWidget() {
		return (LongTextBox) super.getWidget();
	}

	@Override
	public boolean isValid() {
		return !isEmpty() ? (getWidget().getLong() != null) : super.isValid();
	}

	
	
}

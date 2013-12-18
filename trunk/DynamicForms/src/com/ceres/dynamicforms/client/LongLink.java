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
		getWidget().setLong((Long) get(item, fieldName));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, fieldName, getWidget().getLong());
	}

	@Override
	protected LongTextBox getWidget() {
		return (LongTextBox) super.getWidget();
	}

	
}

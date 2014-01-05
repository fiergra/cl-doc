package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.FloatTextBox;

public class FloatLink extends TextLink {

	public FloatLink(Interactor interactor, String fieldName, FloatTextBox widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setFloat((Float) get(item, name));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getFloat());
	}

	@Override
	public FloatTextBox getWidget() {
		return (FloatTextBox) super.getWidget();
	}

	
}

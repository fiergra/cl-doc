package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.ceres.dynamicforms.client.components.FloatTextBox;

public class FloatLink extends TextLink {

	public FloatLink(Interactor interactor, String fieldName, FloatTextBox widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(INamedValues item) {
		getWidget().setFloat((Float) item.getValue(fieldName));
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setValue(fieldName, getWidget().getFloat());
	}

	@Override
	protected FloatTextBox getWidget() {
		return (FloatTextBox) super.getWidget();
	}

	
}

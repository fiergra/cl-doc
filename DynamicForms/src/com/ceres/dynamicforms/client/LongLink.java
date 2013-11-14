package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.ceres.dynamicforms.client.components.LongTextBox;

public class LongLink extends TextLink {

	public LongLink(Interactor interactor, String fieldName, LongTextBox widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(INamedValues item) {
		getWidget().setLong((Long) item.getValue(fieldName));
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setValue(fieldName, getWidget().getLong());
	}

	@Override
	protected LongTextBox getWidget() {
		return (LongTextBox) super.getWidget();
	}

	
}

package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBoxBase;

public class TextLink extends InteractorWidgetLink<Map<String, Serializable>> {

	public TextLink(final Interactor<Map<String, Serializable>> interactor, String fieldName, TextBoxBase widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
		widget.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(TextLink.this);
			}
		});
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		Serializable value = get(item, name);
		if (value instanceof String || value == null) {
			getWidget().setText((String)value);
		} else {
			GWT.log("cannot cast " + value.getClass().getCanonicalName() + " to String.");
		}
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		String text = getWidget().getText();
		put(item, name, text != null && text.length() > 0 ? text : null);
	}

	@Override
	public TextBoxBase getWidget() {
		return (TextBoxBase) super.getWidget();
	}

	@Override
	public boolean isEmpty() {
		return getWidget().getText() == null || getWidget().getText().length() == 0;
	}

	
	
}

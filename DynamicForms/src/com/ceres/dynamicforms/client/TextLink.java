package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBoxBase;

public class TextLink extends InteractorLink {

	public TextLink(final Interactor interactor, String fieldName, TextBoxBase widget, HashMap<String, String> attributes) {
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
		getWidget().setText((String) get(item, name));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getText());
	}

	@Override
	protected TextBoxBase getWidget() {
		return (TextBoxBase) super.getWidget();
	}

	@Override
	public boolean isEmpty() {
		return getWidget().getText() == null || getWidget().getText().length() == 0;
	}

	
	
}

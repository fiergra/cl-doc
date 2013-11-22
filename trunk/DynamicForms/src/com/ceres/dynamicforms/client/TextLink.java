package com.ceres.dynamicforms.client;

import java.util.HashMap;

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
	public void toDialog(INamedValues item) {
		getWidget().setText((String) item.getValue(fieldName));
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setValue(fieldName, getWidget().getText());
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

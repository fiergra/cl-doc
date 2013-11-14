package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class BooleanLink extends InteractorLink {

	public BooleanLink(final Interactor interactor, String fieldName, CheckBox widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
		widget.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				interactor.onChange(BooleanLink.this);
			}
		});
	}

	@Override
	public void toDialog(INamedValues item) {
		getWidget().setValue(Boolean.TRUE.equals(item.getValue(fieldName)));
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setValue(fieldName, getWidget().getValue());
	}

	@Override
	protected CheckBox getWidget() {
		return (CheckBox) super.getWidget();
	}

	
}

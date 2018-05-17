package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class BooleanLink extends InteractorWidgetLink {

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
	public void toDialog(Map<String, Serializable> item) {
		Serializable sValue = get(item, name);
		boolean bValue;
		
		if (sValue instanceof Number) {
			bValue = ((Number)sValue).intValue() != 0;
		} else {
			bValue = Boolean.TRUE.equals(sValue);
		}
		getWidget().setValue(bValue);
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getValue());
	}

	@Override
	public CheckBox getWidget() {
		return (CheckBox) super.getWidget();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	
}

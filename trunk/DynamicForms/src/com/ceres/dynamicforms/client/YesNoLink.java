package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.YesNoRadioGroup;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class YesNoLink extends InteractorLink {

	public YesNoLink(final Interactor interactor, String fieldName, YesNoRadioGroup widget,
			HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
		widget.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(YesNoLink.this);
			}
		});
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setValue(Boolean.TRUE.equals( get(item, name)), false);
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		put(item, name, getWidget().getValue());
	}

	@Override
	protected YesNoRadioGroup getWidget() {
		return (YesNoRadioGroup) super.getWidget();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	
}

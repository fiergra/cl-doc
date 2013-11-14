package com.ceres.dynamicforms.client;

import java.util.HashMap;

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
	public void toDialog(INamedValues item) {
		getWidget().setValue(Boolean.TRUE.equals(item.getValue(fieldName)), false);
	}

	@Override
	public void fromDialog(INamedValues item) {
		item.setValue(fieldName, getWidget().getValue());
	}

	@Override
	protected YesNoRadioGroup getWidget() {
		return (YesNoRadioGroup) super.getWidget();
	}

	
}

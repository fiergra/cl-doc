package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;

public class PushButtonLink extends InteractorWidgetLink {

	public PushButtonLink(final Interactor interactor, String name, PushButton widget, HashMap<String, String> attributes) {
		super(interactor, name, widget, attributes);
		
		widget.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				interactor.onChange(PushButtonLink.this);
			}
		});

		
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public PushButton getWidget() {
		return (PushButton) super.getWidget();
	}
	
	

}

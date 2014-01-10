package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

public class PassiveInteractorLink extends InteractorWidgetLink {

	public PassiveInteractorLink(Interactor interactor, String fieldName, Widget widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(Map<String, Serializable> item){};
	@Override
	public void fromDialog(Map<String, Serializable> item){}

	@Override
	public boolean isEmpty() {
		return true;
	};
}

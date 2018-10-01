package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Widget;

public class PassiveInteractorLink<T> extends InteractorWidgetLink<T> {

	public PassiveInteractorLink(Interactor interactor, String fieldName, Widget widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(T item){};
	@Override
	public void fromDialog(T item){}

	@Override
	public boolean isEmpty() {
		return true;
	};
}

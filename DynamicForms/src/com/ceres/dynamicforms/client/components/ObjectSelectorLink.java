package com.ceres.dynamicforms.client.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorWidgetLink;
import com.google.gwt.user.client.ui.Widget;

public class ObjectSelectorLink<T extends Serializable> extends InteractorWidgetLink<Map<String, Serializable>> {

	public ObjectSelectorLink(Interactor interactor, String name, Widget widget, HashMap<String, String> attributes) {
		super(interactor, name, widget, attributes);
	}

	@SuppressWarnings("unchecked")
	public ObjectSelectorComboBox<T> getWidget() {
		return (ObjectSelectorComboBox<T>) widget;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void toDialog(Map<String, Serializable> act) {
		getWidget().setValue((T) act.get(getName()));
	}
	
	@Override
	public boolean isEmpty() {
		return getWidget().getValue() == null;
	}
	
	@Override
	public void fromDialog(Map<String, Serializable> act) {
		act.put(getName(), getWidget().getValue());
	}

}

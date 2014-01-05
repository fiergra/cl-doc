package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

public abstract class InteractorLink {
	protected final String name;
	protected final Widget widget;
	protected final HashMap<String, String> attributes;
	protected final Interactor interactor;
	
	protected final boolean isRequired;

	public InteractorLink(Interactor interactor, String name, Widget widget, HashMap<String, String> attributes) {
		this.interactor = interactor;
		this.name = name;
		this.widget = widget;
		this.attributes = attributes;
		
		isRequired = attributes != null && "true".equals(attributes.get("required")); 
		
	}

	public abstract void toDialog(Map<String, Serializable> item);
	public abstract void fromDialog(Map<String, Serializable> item);
	
	public Widget getWidget() {
		return widget;
	}

	public abstract boolean isEmpty();
	
	protected Serializable get(Map<String, Serializable> item, String fieldName) {
		Serializable value;
		int index = fieldName.indexOf('.');
		if (index == -1) {
			value = item.get(fieldName);
		} else {
			Map<String, Serializable> subItem = (Map<String, Serializable>)item.get(fieldName.substring(0, index));
			value = get(subItem, fieldName.substring(index + 1));
		}
		
		return value;
	}

	void put(Map<String, Serializable> item, String fieldName, Serializable value) {
		int index = fieldName.indexOf('.');
		if (index == -1) {
			item.put(fieldName, value);
		} else {
			Map<String, Serializable> subItem = (Map<String, Serializable>)item.get(fieldName.substring(0, index));
			put(subItem, fieldName.substring(index + 1), value);
		}
	}

	public boolean isValid() {
		return isRequired ? !isEmpty() : true;
	}

	public String getName() {
		return name;
	}

}

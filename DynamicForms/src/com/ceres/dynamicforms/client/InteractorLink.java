package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Map;

public abstract class InteractorLink {
	protected final String name;
	protected final Interactor interactor;

	public InteractorLink(Interactor interactor, String name) {
		this.interactor = interactor;
		this.name = name;
	}

	public abstract void toDialog(Map<String, Serializable> item);
	public abstract void fromDialog(Map<String, Serializable> item);
	protected abstract void hilite(boolean isValid);
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

	protected void put(Map<String, Serializable> item, String fieldName, Serializable value) {
		int index = fieldName.indexOf('.');
		if (index == -1) {
			item.put(fieldName, value);
		} else {
			Map<String, Serializable> subItem = (Map<String, Serializable>)item.get(fieldName.substring(0, index));
			put(subItem, fieldName.substring(index + 1), value);
		}
	}

	public boolean isValid() {
		return true;
	}

	public String getName() {
		return name;
	}

}

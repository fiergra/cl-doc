package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.components.DateTextBox;

public class ItemDateLink extends DateLink {

	public ItemDateLink(Interactor interactor, String fieldName, DateTextBox widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
		getWidget().setDate((Date) item.get(name));
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
		item.put(name, getWidget().getDate());
	}

}

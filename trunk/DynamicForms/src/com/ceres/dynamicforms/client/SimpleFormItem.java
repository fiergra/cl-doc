package com.ceres.dynamicforms.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


public class SimpleFormItem extends Panel  {

	public Widget content;
	private SimpleForm simpleForm;
	private int row;

	private final HashMap<String, String> attributes;
	
	public SimpleFormItem(HashMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setSimpleForm(SimpleForm simpleForm) {
		this.simpleForm = simpleForm;
		this.row = simpleForm.getRowCount();
	}
	
	@Override
	public void add(Widget child) {
		this.content = child;
		if (child != null) {
			this.content.addStyleName("simpleFormItem");
			simpleForm.setWidget(row, 1, this.content);
		}

	}

	@Override
	public Iterator<Widget> iterator() {
		Collection<Widget> collection = new ArrayList<Widget>();
		collection.add(content);
		
		return collection.iterator();
	}

	@Override
	public boolean remove(Widget child) {
		boolean removed = false;
		
		if (content == child) {
			content = null;
			removed = true;
		}
		return removed;
	}

	@Override
	public void setHeight(String height) {
	}

	@Override
	public void setWidth(String width) {
	}

	public String getAttribute(String key) {
		return attributes != null ? attributes.get(key) : null;
	}
	
	
}

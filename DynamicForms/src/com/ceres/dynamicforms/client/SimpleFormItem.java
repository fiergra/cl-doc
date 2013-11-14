package com.ceres.dynamicforms.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


public class SimpleFormItem extends Panel  {

	public String label;

	public Widget content;
	private SimpleForm simpleForm;
	private int row;
	
	public SimpleFormItem(String label) {
		this.label = label;
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
	
	
}

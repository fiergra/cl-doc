package com.ceres.dynamicforms.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
		if (this.content == null) {
			this.content = child;
		} else {
			if (this.content instanceof Panel) {
				((Panel)this.content).add(child);
			} else {
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				vp.add(this.content);
				vp.add(child);
				this.content = vp;
			}
		}
		if (child != null) {
//			this.content.addStyleName("simpleFormItem");
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

	@Override
	public void setTitle(String title) {
	}

	public String getAttribute(String key) {
		return attributes != null ? attributes.get(key) : null;
	}
	
	
}

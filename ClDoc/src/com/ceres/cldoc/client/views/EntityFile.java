package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Entity;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EntityFile <T extends Entity> extends LayoutPanel {

	private final T entity;
	private boolean isModified;
	
	public EntityFile(ClDoc clDoc, T entity, Widget header, String config) {
		super();
		this.entity = entity;
		add(header);
		setWidgetLeftWidth(header, 220, Unit.PX, 100, Unit.PCT);
		TabLayoutPanel tab = new ConfiguredTabPanel<T>(clDoc, config, entity);
		tab.addStyleName("personalFile");
		add(tab);
	}

	public T getEntity() {
		return entity;
	}

	public void setModified(boolean set) {
		isModified = set;
	}
	
	public boolean isModified() {
		return isModified;
	}
}

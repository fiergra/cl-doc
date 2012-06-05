package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Entity;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EntityFile <T extends Entity> extends DockLayoutPanel {

	private T entity;

	public EntityFile(ClDoc clDoc, T entity, Widget header, String config) {
		super(Unit.PT);
		this.entity = entity;
		addNorth(header, 22);
		TabLayoutPanel tab = new ConfiguredTabPanel<T>(clDoc, config, entity);
		tab.addStyleName("personalFile");
		add(tab);
	}

	public T getEntity() {
		return entity;
	}

	
}

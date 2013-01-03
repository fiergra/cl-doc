package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.PersonDetails;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
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
		setWidgetHorizontalPosition(header, Alignment.END);
		TabLayoutPanel tab = new TabLayoutPanel(2.5, Unit.EM);
		tab.add(new HistoryView(clDoc, entity, tab), "Formulare");
		if (entity instanceof Person) {
			tab.add(new PersonDetails(clDoc, (Person)entity), "Stammdaten");
		}
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

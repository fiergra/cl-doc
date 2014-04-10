package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.PersonDetails;
import com.ceres.cldoc.model.IEntity;
import com.ceres.cldoc.model.Patient;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PatientWrapper;
import com.ceres.cldoc.shared.domain.PersonWrapper;
import com.ceres.dynamicforms.client.Interactor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EntityFile <T extends IEntity> extends LayoutPanel {

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
			PersonWrapper personWrapper = entity instanceof Patient ? new PatientWrapper((Patient) entity) : new PersonWrapper((Person) entity);
			Interactor ia = new Interactor();
			Widget pd = PersonDetails.create(clDoc, (Person) entity, ia);
			tab.add(pd, "Stammdaten");
			ia.toDialog(personWrapper);
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

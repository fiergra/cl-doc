package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Person;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class PersonalFile extends DockLayoutPanel {

	private Person humanBeing;

	public PersonalFile(ClDoc clDoc, Person hb) {
		super(Unit.PX);
		this.humanBeing = hb;
		addNorth(new PersonalFileHeader(hb), 42);
		TabLayoutPanel tab = new ConfiguredTabPanel<Person>(clDoc, "CLDOC.PERSONALFILE", humanBeing);
		tab.addStyleName("personalFile");
		add(tab);
	}

	public Person getHumanBeing() {
		return humanBeing;
	}

	
}
package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class PersonalFileHeader extends HorizontalPanel {

	private Person hb;

	public PersonalFileHeader(Person hb) {
		this.hb = hb;
		setup();
	}

	private void setup() {
		setSpacing(5);
		setVerticalAlignment(ALIGN_MIDDLE);
		Label nameAndId = new Label(hb.id + " " + hb.lastName + ", " + hb.firstName);
		nameAndId.setStylePrimaryName("nameAndId");
		add(nameAndId);
	}

}

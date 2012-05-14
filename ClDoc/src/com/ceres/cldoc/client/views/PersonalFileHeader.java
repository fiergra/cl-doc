package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Person;
import com.google.gwt.i18n.client.DateTimeFormat;
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
		if (hb.dateOfBirth != null) {
			String sbd = DateTimeFormat.getFormat("dd.MM.yyyy").format(hb.dateOfBirth);
			Label birthDate = new Label(" *" + sbd);
			birthDate.setStylePrimaryName("nameAndId");
			add(birthDate);
		}
		
		if (hb.gender != null) {
			
		}
		
	}

}

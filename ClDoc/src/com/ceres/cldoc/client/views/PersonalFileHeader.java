package com.ceres.cldoc.client.views;

import java.util.Date;

import com.ceres.cldoc.model.Person;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PersonalFileHeader extends HorizontalPanel {

	private final Person hb;

	public PersonalFileHeader(Person hb) {
		this.hb = hb;
		setup();
	}

	private void setup() {
		setSpacing(5);
		setVerticalAlignment(ALIGN_MIDDLE);
		Label nameAndId = new Label(hb.perId + " " + hb.lastName + ", " + hb.firstName);
		nameAndId.setStylePrimaryName("nameAndId");
		add(nameAndId);
		if (hb.dateOfBirth != null) {
			String sbd = DateTimeFormat.getFormat("dd.MM.yyyy").format(hb.dateOfBirth);
			Label birthDate = new Label(" *" + sbd);
			birthDate.setStylePrimaryName("nameAndId");
			add(birthDate);
			
			long ageMs = new Date().getTime() - hb.dateOfBirth.getTime();
		}
		
		if (hb.gender != null) {
			Image gender = hb.gender.id.equals(152l) ? new Image("icons/male-sign.png") : new Image("icons/female-sign.png");
			gender.setHeight("1.5em");
			add(gender);
		}
		
	}

}

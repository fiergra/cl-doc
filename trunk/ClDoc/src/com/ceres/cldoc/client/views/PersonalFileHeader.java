package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class PersonalFileHeader extends HorizontalPanel {

	private HumanBeing hb;

	public PersonalFileHeader(HumanBeing hb) {
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
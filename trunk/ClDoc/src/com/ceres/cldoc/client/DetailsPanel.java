package com.ceres.cldoc.client;

import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DetailsPanel extends VerticalPanel {

	private Person humanBeing;

	public DetailsPanel(Person model) {
		this.humanBeing = model;
	}

}

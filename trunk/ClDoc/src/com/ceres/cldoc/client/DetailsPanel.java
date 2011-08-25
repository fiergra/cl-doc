package com.ceres.cldoc.client;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DetailsPanel extends VerticalPanel {

	private HumanBeing humanBeing;

	public DetailsPanel(HumanBeing model) {
		this.humanBeing = model;
	}

}

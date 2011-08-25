package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class PersonalFile extends DockLayoutPanel {

	private HumanBeing humanBeing;

	public PersonalFile(HumanBeing hb) {
		super(Unit.EM);
		this.humanBeing = hb;
		addNorth(new PersonalFileHeader(hb), 3);
		TabLayoutPanel tab = new ConfiguredTabPanel<HumanBeing>("PERSONALFILE", humanBeing);
		tab.addStyleName("personalFile");
		add(tab);
	}

	public HumanBeing getHumanBeing() {
		return humanBeing;
	}

	
}

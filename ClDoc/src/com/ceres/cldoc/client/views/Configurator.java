package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Configurator extends TabLayoutPanel {

	public Configurator(ClDoc clDoc) {
		super(2, Unit.EM);
		
		add(new Styler(clDoc), SRV.c.forms());
		add(new CatalogConfigurator(clDoc), SRV.c.catalogs());
		add(new EntityConfigurator(clDoc), "Entities");
	}

}

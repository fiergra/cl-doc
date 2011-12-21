package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Configurator extends TabLayoutPanel {

	public Configurator(ClDoc clDoc) {
		super(2, Unit.EM);
		
		add(new Styler(clDoc), "Formulare");
		add(new CatalogConfigurator(clDoc), "Catalogs");
		add(new CatalogConfigurator(clDoc), "Assignments");
	}

}

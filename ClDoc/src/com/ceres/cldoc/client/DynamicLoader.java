package com.ceres.cldoc.client;

import com.ceres.cldoc.client.views.AssignmentsPanel;
import com.ceres.cldoc.client.views.CatalogConfigurator;
import com.ceres.cldoc.client.views.Configurator;
import com.ceres.cldoc.client.views.DebugPanel;
import com.ceres.cldoc.client.views.EntityConfigurator;
import com.ceres.cldoc.client.views.EntitySearch;
import com.ceres.cldoc.client.views.HistoryView;
import com.ceres.cldoc.client.views.Persons;
import com.ceres.cldoc.client.views.Reporting;
import com.ceres.cldoc.client.views.Styler;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DynamicLoader {
	public static <T> Widget create(ClDoc clDoc, Catalog catalog, T model) {
		Widget result = null;
		String name = catalog.text;
		
		if (name.equals("Personen")) {
			result = new Persons((ClDoc) model);
		} else if (name.equals("Suche")) {
			result = new EntitySearch((ClDoc) model, catalog.number1);
		} else if (name.equals("Configuration")) {
			result = new Configurator((ClDoc) model);
		} else if (name.equals("Formulare")) {
			result = new HistoryView(clDoc, (Entity) model);
		} else if (name.equals("Stammdaten")) {
			if (model instanceof Person) {
				result = new PersonDetails(clDoc, (Person) model);
			} else if (model instanceof Entity){
				result = new EntityDetails(clDoc, (Entity)model);
			} else {
				result = new Label("details...");
			}
		} else if (name.equals("Reporting")) {
			result = new Reporting(clDoc);
		} else if (name.equals("Debug")) {
			result = new DebugPanel(clDoc);
		} else if (name.equals("Kataloge")) {
			result = new CatalogConfigurator(clDoc);
		} else if (name.equals("Layouts")) {
			result = new Styler(clDoc);
		} else if (name.equals("Entitaeten")) {
			result = new EntityConfigurator(clDoc);
		} else if (name.equals("Berechtigungen")) {
			result = new AssignmentsPanel(clDoc);
		} else {
			HorizontalPanel hp = new HorizontalPanel();
			Label l = new Label(name);
			l.setSize("100%", "100%");
			hp.add(l);
			result = hp;
		}
		
//		add(new Styler(clDoc), SRV.c.forms());
//		add(new CatalogConfigurator(clDoc), SRV.c.catalogs());
//		add(new EntityConfigurator(clDoc), "Entities");


//		result.setSize("100%", "100%");
		
		return result;
	}
}

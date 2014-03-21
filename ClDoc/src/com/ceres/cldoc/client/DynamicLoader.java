package com.ceres.cldoc.client;

import com.ceres.cldoc.client.timemanagement.TimeSheet;
import com.ceres.cldoc.client.views.AssignmentsPanel;
import com.ceres.cldoc.client.views.CatalogConfigurator2;
import com.ceres.cldoc.client.views.Configurator;
import com.ceres.cldoc.client.views.DebugPanel;
import com.ceres.cldoc.client.views.EntityConfigurator;
import com.ceres.cldoc.client.views.EntitySearch;
import com.ceres.cldoc.client.views.LeaveRegistration;
import com.ceres.cldoc.client.views.OrganisationsPanel;
import com.ceres.cldoc.client.views.Persons;
import com.ceres.cldoc.client.views.Reporting;
import com.ceres.cldoc.client.views.SettingsPanel;
import com.ceres.cldoc.client.views.Styler;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
//import com.ceres.cldoc.client.views.agenda.CalendarView;

public class DynamicLoader {
	public static Widget create(ClDoc clDoc, Catalog catalog) {
		Widget result = null;
		String name = catalog.text;
		String code = catalog.code;
		
		if (name.equals("Personen")) {
			result = new Persons(clDoc);
		} else if (catalog.code.equals("SucheDKG") || catalog.code.equals("SucheEART")) {
			result = new EntitySearch(clDoc, clDoc.getSession().getUser().getOrganisation().getId());
		} else if (name.equals("Configuration")) {
			result = new Configurator(clDoc);
		} else if (code.equals("LeaveRegistration")) {
			result = new LeaveRegistration(clDoc);
		} else if (code.equals("TimeSheet")) {
			result = new TimeSheet(clDoc);
//		} else if (name.equals("Formulare")) {
//			result = new HistoryView(clDoc, (Entity) model);
//		} else if (name.equals("Stammdaten")) {
//			if (model instanceof Person) {
//				result = new PersonDetails(clDoc, (Person) model);
//			} else if (model instanceof Entity){
//				result = new EntityDetails(clDoc, (Entity)model);
//			} else {
//				result = new Label("details...");
//			}
		} else if (name.equals("Reporting")) {
			result = new Reporting(clDoc);
		} else if (name.equals("Debug")) {
			result = new DebugPanel(clDoc);
		} else if (name.equals("Einstellungen")) {
			result = new SettingsPanel(clDoc);
		} else if (name.equals("Kataloge")) {
			result = new CatalogConfigurator2(clDoc);
		} else if (name.equals("Layouts")) {
			result = new Styler(clDoc);
		} else if (name.equals("Entitaeten")) {
			result = new EntityConfigurator(clDoc);
		} else if (name.equals("Berechtigungen")) {
			result = new AssignmentsPanel(clDoc);
		} else if (name.equals("Organisation")) {
			result = new OrganisationsPanel(clDoc, clDoc.getSession().getUser().getOrganisation());
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

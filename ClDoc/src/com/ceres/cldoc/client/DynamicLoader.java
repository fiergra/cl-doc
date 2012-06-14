package com.ceres.cldoc.client;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.Configurator;
import com.ceres.cldoc.client.views.DebugPanel;
import com.ceres.cldoc.client.views.EntitySearch;
import com.ceres.cldoc.client.views.HistoryView;
import com.ceres.cldoc.client.views.Persons;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DynamicLoader {
	public static <T> Widget create(ClDoc clDoc, String name, T model) {
		Widget result = null;
		
		if (name.equals("Personen")) {
			name = SRV.c.persons();
			result = new Persons((ClDoc) model);
		} else if (name.equals("Suche")) {
			result = new EntitySearch((ClDoc) model, 1001);
		} else if (name.equals("Configuration")) {
			name = SRV.c.configuration();
			result = new Configurator((ClDoc) model);
		} else if (name.equals("HISTORY")) {
			name = SRV.c.history();
			result = new HistoryView(clDoc, (Entity) model);
		} else if (name.equals("DETAILS")) {
			name = SRV.c.details();
			if (model instanceof Person) {
				result = new PersonDetails(clDoc, (Person) model);
			} else if (model instanceof Entity){
				result = new EntityDetails(clDoc, (Entity)model);
			} else {
				result = new Label("details...");
			}
		} else if (name.equals("Reporting")) {
			result = new DebugPanel(clDoc);
		} else {
			HorizontalPanel hp = new HorizontalPanel();
			Label l = new Label(name);
			l.setSize("100%", "100%");
			hp.add(l);
			result = hp;
		}

//		result.setSize("100%", "100%");
		
		return result;
	}
}

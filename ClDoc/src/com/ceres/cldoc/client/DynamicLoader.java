package com.ceres.cldoc.client;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.Configurator;
import com.ceres.cldoc.client.views.HistoryView;
import com.ceres.cldoc.client.views.Home;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DynamicLoader {
	public static <T> Widget create(Session session, String name, T model) {
		Widget result = null;
		
		if (name.equals("HOME")) {
			name = SRV.c.home();
			result = new Home((ClDoc) model);
		} else if (name.equals("CONFIG")) {
			name = SRV.c.configuration();
			result = new Configurator((ClDoc) model);
		} else if (name.equals("HISTORY")) {
			name = SRV.c.history();
			result = new HistoryView(session, (Person) model);
		} else if (name.equals("DETAILS")) {
			name = SRV.c.details();
			result = new PersonDetails(session, (Person) model);
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

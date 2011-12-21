package com.ceres.cldoc.client;

import com.ceres.cldoc.client.views.Configurator;
import com.ceres.cldoc.client.views.HistoryView;
import com.ceres.cldoc.client.views.Home;
import com.ceres.cldoc.client.views.PersonEditor;
import com.ceres.cldoc.client.views.Styler;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.PersonWrapper;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DynamicLoader {
	public static <T> Widget create(String name, T model) {
		Widget result = null;
		
		if (name.equals("HOME")) {
			result = new Home((ClDoc) model);
		} else if (name.equals("CONFIG")) {
			result = new Configurator((ClDoc) model);
		} else if (name.equals("HISTORY")) {
			result = new HistoryView((HumanBeing) model);
		} else if (name.equals("DETAILS")) {
			result = new PersonDetails((HumanBeing) model);
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

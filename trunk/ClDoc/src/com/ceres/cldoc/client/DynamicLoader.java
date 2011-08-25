package com.ceres.cldoc.client;

import com.ceres.cldoc.client.views.Home;
import com.ceres.cldoc.client.views.Styler;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DynamicLoader {
	public static <T> Widget create(String name, T model) {
		Widget result = null;
		
		if (name.equals("HOME")) {
			result = new Home((ClDoc) model);
		} else if (name.equals("DEBUG")) {
			result = new Styler((ClDoc) model);
		} else if (name.equals("HISTORY")) {
			result = new HistoryView((HumanBeing) model);
		} else if (name.equals("DETAILS")) {
			result = new DetailsPanel((HumanBeing) model);
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

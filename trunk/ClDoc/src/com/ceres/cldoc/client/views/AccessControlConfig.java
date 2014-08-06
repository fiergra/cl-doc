package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class AccessControlConfig extends SplitLayoutPanel {
	
	public AccessControlConfig(ClDoc application) {
		addSouth(new PoliciesPanel(application), 300);
		add(new AssignmentsPanel(application));
	}
}

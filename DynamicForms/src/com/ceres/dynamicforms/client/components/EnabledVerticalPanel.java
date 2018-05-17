package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EnabledVerticalPanel extends VerticalPanel implements HasEnabled {
	private boolean enabled = true;

	@Override
	public boolean isEnabled() {
		return this.enabled ;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (Widget c:getChildren()) {
			if (c instanceof HasEnabled) {
				((HasEnabled)c).setEnabled(enabled);
			}
		}
	}

	@Override
	public void add(Widget w) {
		super.add(w);
		if (w instanceof HasEnabled) {
			((HasEnabled)w).setEnabled(enabled);
		}
	}
	
}

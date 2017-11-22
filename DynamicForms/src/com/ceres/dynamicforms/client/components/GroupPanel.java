package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface GroupPanel extends IsWidget, HasEnabled {

	HorizontalPanel getHeader();

	void setBusy(boolean isBusy);

}

package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface GroupPanel extends IsWidget{

	HorizontalPanel getHeader();

	void setBusy(boolean isBusy);

}

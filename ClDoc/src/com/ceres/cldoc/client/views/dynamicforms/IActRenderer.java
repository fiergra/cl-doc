package com.ceres.cldoc.client.views.dynamicforms;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.user.client.ui.IsWidget;

public interface IActRenderer extends IsWidget {

	boolean setAct(LayoutDefinition ld, Act act);

}

package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IAct;
import com.google.gwt.user.client.ui.IsWidget;

public interface IForm extends IView, IsWidget {
	IAct getModel();
	void fromDialog();
	void toDialog();
	
	boolean isModified();
	void clearModification();
}

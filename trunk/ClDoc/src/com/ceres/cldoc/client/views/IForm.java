package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IAct;
import com.google.gwt.user.client.ui.IsWidget;

public interface IForm extends IView, IsWidget {
	@Override
	IAct getModel();
	void setModel(IAct act);
	@Override
	void fromDialog();
	@Override
	void toDialog();
	
	@Override
	boolean isModified();
	@Override
	void clearModification();
	boolean isValid();
	ClInteractor getInteractor();
}

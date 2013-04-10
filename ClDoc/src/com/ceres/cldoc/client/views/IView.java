package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IAct;

public interface IView {
	IAct getModel();
	void fromDialog();
	void toDialog();
	
	boolean isModified();
	void clearModification();
}

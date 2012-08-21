package com.ceres.cldoc.client.controls;

import com.google.gwt.user.client.ui.IsWidget;

public interface InteractiveControl extends IsWidget {
	void setMandatory();
	boolean isMandatory();
	boolean isValid();
	boolean validate();
}

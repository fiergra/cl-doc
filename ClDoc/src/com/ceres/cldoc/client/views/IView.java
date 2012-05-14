package com.ceres.cldoc.client.views;

import com.google.gwt.user.client.ui.IsWidget;


public interface IView<T> extends IsWidget {
	T getModel();
	void fromDialog();
	void toDialog();
	boolean isModified();
}

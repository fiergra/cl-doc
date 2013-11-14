package com.ceres.cldoc.client.views;

import java.io.Serializable;

import com.google.gwt.event.dom.client.ChangeHandler;

public interface IEntitySelector<T extends Serializable> {
	boolean setSelected(T entity);
	T getSelected();
	void addSelectionChangedHandler(ChangeHandler changeHandler);
}

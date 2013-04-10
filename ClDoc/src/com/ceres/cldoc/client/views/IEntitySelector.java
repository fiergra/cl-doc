package com.ceres.cldoc.client.views;

import com.google.gwt.event.dom.client.ChangeHandler;



public interface IEntitySelector<T> {
	boolean setSelected(T entity);
	T getSelected();
	void addSelectionChangedHandler(ChangeHandler changeHandler);
}

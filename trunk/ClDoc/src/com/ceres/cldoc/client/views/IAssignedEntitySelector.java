package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Catalog;



public interface IAssignedEntitySelector<T> {
	boolean setSelected(T entity);
	T getSelected();
	Catalog getRole();
}

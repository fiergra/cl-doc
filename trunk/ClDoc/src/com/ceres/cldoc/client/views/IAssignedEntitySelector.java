package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Catalog;
import com.ceres.core.IEntity;



public interface IAssignedEntitySelector<T> {
	boolean setSelected(IEntity entity);
	T getSelected();
	Catalog getRole();
}

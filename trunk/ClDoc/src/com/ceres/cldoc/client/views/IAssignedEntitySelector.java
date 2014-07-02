package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;



public interface IAssignedEntitySelector<T> {
	boolean setSelected(Entity entity);
	T getSelected();
	Catalog getRole();
}

package com.ceres.cldoc.client.views;


public interface IEntitySelector<T> {
	boolean setSelected(T entity);
	T getSelected();
}

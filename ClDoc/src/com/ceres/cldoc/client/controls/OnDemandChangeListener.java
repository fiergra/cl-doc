package com.ceres.cldoc.client.controls;

public interface OnDemandChangeListener<T> {
	void onChange(T oldValue, T newValue);
}

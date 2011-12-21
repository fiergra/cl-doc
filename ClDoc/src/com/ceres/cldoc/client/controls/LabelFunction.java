package com.ceres.cldoc.client.controls;

public interface LabelFunction<T> {
	String getLabel(T item);
	String getValue(T item);
}

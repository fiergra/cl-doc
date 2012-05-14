package com.ceres.cldoc.client.controls;

public interface LabelFunction<T> {
	String getLabel(T act);
	String getValue(T act);
}

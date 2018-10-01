package com.ceres.dynamicforms.client;

public interface ITranslator<T> {
	String getLabel(String key);
	boolean isVisible(T item, String objectType);
	boolean isEnabled(T item, String objectType);
}

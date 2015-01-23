package com.ceres.dynamicforms.client;

public interface ITranslator {
	String getLabel(String key);
	boolean isVisible(String id);
	boolean isEnabled(String id);
}

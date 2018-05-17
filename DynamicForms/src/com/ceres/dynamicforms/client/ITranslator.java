package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Map;

public interface ITranslator {
	String getLabel(String key);
	boolean isVisible(Map<String, Serializable> item, String objectType);
	boolean isEnabled(Map<String, Serializable> item, String objectType);
}

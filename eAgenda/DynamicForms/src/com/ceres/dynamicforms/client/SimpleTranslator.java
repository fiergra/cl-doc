package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Map;

public class SimpleTranslator implements ITranslator {

	@Override
	public String getLabel(String key) {
		return key;
	}

	@Override
	public boolean isVisible(Map<String, Serializable> item, String id) {
		return true;
	}

	@Override
	public boolean isEnabled(Map<String, Serializable> item, String id) {
		return true;
	}

}

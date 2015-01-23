package com.ceres.dynamicforms.client;

public class SimpleTranslator implements ITranslator {

	@Override
	public String getLabel(String key) {
		return key;
	}

	@Override
	public boolean isVisible(String id) {
		return true;
	}

	@Override
	public boolean isEnabled(String id) {
		return true;
	}

}

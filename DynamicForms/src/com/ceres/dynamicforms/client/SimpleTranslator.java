package com.ceres.dynamicforms.client;

public class SimpleTranslator<T> implements ITranslator<T> {

	@Override
	public String getLabel(String key) {
		return key;
	}

	@Override
	public boolean isVisible(T item, String id) {
		return true;
	}

	@Override
	public boolean isEnabled(T item, String id) {
		return true;
	}

}

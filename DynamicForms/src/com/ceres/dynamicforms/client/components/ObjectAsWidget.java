package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.Label;


public class ObjectAsWidget<T> extends Label {
	final T object;
	
	public ObjectAsWidget(T object) {
		setVisible(false);
		setPixelSize(0, 0);
		this.object = object;
	}

	public T getObject() {
		return object;
	}
}

package com.ceres.cldoc.client.views;

import com.google.gwt.user.client.ui.Frame;

public class FrameView<T> extends Frame implements IView<T> {

	private T model;

	public FrameView(T model, String url) {
		super(url);
		this.model = model;
	}
	
	@Override
	public T getModel() {
		return model;
	}

	@Override
	public void fromDialog() {
	}

	@Override
	public void toDialog() {
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void clearModification() {
	}

}

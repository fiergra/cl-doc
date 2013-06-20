package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IAct;
import com.google.gwt.user.client.ui.Frame;

public class FrameView extends Frame implements IForm {

	private IAct model;

	public FrameView(IAct model, String url) {
		super(url);
		this.model = model;
	}
	
	@Override
	public IAct getModel() {
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
	public boolean isValid() {
		return true;
	}

	@Override
	public void clearModification() {
	}

	@Override
	public void setModel(IAct act) {
		model = act;
	}

}

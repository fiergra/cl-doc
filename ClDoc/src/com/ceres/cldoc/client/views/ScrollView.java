package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IAct;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScrollView extends ScrollPanel implements IForm {

	private final IForm content;

	public ScrollView(IForm content) {
		super((Widget) content);
		this.content = content;
	}
	
	@Override
	public IAct getModel() {
		return content.getModel();
	}

	@Override
	public void fromDialog() {
		content.fromDialog();
	}

	@Override
	public void toDialog() {
		content.toDialog();
	}

	@Override
	public boolean isModified() {
		return content.isModified();
	}

	@Override
	public void clearModification() {
		content.clearModification();
	}

}

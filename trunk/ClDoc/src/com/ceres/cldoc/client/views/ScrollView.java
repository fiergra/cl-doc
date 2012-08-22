package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Act;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScrollView<T> extends ScrollPanel implements IView<T> {

	private IView<T> content;

	public ScrollView(IView<T> content) {
		super((Widget) content);
		this.content = content;
	}
	
	@Override
	public T getModel() {
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

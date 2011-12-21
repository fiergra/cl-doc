package com.ceres.cldoc.client.views;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class DefaultCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		VerticalPanel vp = new VerticalPanel();
		
		vp.add(new HTML("<b>" + caught.getLocalizedMessage() + "</b>"));
		StackTraceElement[] elements = caught.getStackTrace();
		
		for (int i = 0; i < elements.length; i++) {
			vp.add(new HTML(elements[i].toString()));
		}
		
		PopupManager.showModal("Error", vp, true);
	}

}

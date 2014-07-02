package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class DefaultCallback<T> implements AsyncCallback<T> {

	private long callId;
	private ClDoc clDoc;

	public DefaultCallback(ClDoc clDoc, String name) {
		this.clDoc = clDoc;
		clDoc.status(name);
		callId = clDoc.startAsyncCall(name);
	}
	
	private DefaultCallback() {
	}
	
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

	@Override
	public void onSuccess(T result) {
		clDoc.stopAsyncCall(callId);
		onResult(result);
	}

	public abstract void onResult(T result);

	
}

package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Widget;

public abstract class InteractorLink {
	protected final String fieldName;
	protected final Widget widget;
	protected final HashMap<String, String> attributes;
	protected final Interactor interactor;

	public InteractorLink(Interactor interactor, String fieldName, Widget widget, HashMap<String, String> attributes) {
		this.interactor = interactor;
		this.fieldName = fieldName;
		this.widget = widget;
		this.attributes = attributes;
	}

	public abstract void toDialog(INamedValues item);
	public abstract void fromDialog(INamedValues item);
	
	protected Widget getWidget() {
		return widget;
	}

}

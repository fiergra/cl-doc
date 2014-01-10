package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

public abstract class InteractorWidgetLink extends InteractorLink {
	protected final Widget widget;
	protected final HashMap<String, String> attributes;
	
	protected final boolean isRequired;

	public InteractorWidgetLink(Interactor interactor, String name, Widget widget, HashMap<String, String> attributes) {
		super(interactor, name);
		this.widget = widget;
		this.attributes = attributes;
		
		isRequired = attributes != null && "true".equals(attributes.get("required")); 
		
	}

	protected void hilite(boolean isValid) {
		if (!isValid) {
			getWidget().addStyleName("invalidContent");
		} else {
			getWidget().removeStyleName("invalidContent");
		}
		
	}
	
	public Widget getWidget() {
		return widget;
	}


	public boolean isValid() {
		return isRequired ? !isEmpty() : true;
	}

}

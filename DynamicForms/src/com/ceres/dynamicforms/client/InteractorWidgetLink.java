package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public abstract class InteractorWidgetLink<T> extends InteractorLink<T> {
	private static final String OBJECT_TYPE = "objectType";
	public static final String FOCUS = "focus";
	public static final String REQUIRED = "required";
	public static final String ENABLED = "enabled";
	public static final String DEFAULT = "default";
	
	protected final Widget widget;
	protected final HashMap<String, String> attributes;
	
	private boolean isRequired;
	private boolean isEnabled;
	private final boolean requestFocus;
	private String objectType;
	private String sDefault;

	public InteractorWidgetLink(Interactor interactor, String name, Widget widget, HashMap<String, String> attributes) {
		super(interactor, name);
		this.widget = widget;
		this.attributes = attributes;
		
		isEnabled =  attributes == null || !("false".equals(attributes.get(ENABLED)));
		isRequired = attributes != null && "true".equals(attributes.get(REQUIRED)); 
		requestFocus = attributes != null && "true".equals(attributes.get(FOCUS));
		objectType = attributes != null ? attributes.get(OBJECT_TYPE) : null;
		sDefault = attributes != null ? attributes.get(DEFAULT) : null;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}

	public String getDefault() {
		return sDefault;
	}

	public void requestFocus() {
		if (requestFocus) {
			if (getWidget() instanceof FocusWidget) {
				Scheduler.get().scheduleDeferred(()->((FocusWidget)getWidget()).setFocus(requestFocus));
			} else  if (getWidget() instanceof Focusable) {
				Scheduler.get().scheduleDeferred(()->((Focusable)getWidget()).setFocus(requestFocus));
			}
		}
		
	}
	
	public void hilite(boolean isValid) {
		if (!isValid) {
			getWidget().addStyleName("invalidContent");
		} else {
			getWidget().removeStyleName("invalidContent");
		}
	}
	
	public Widget getWidget() {
		return widget;
	}

	public void setRequired(boolean required) {
		isRequired = required;
		interactor.onChange(this);
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
		interactor.onChange(this);
	}

	public boolean isValid() {
		return isRequired ? !isEmpty() : true;
	}

	@Override
	public void enable(boolean enabled) {
		if (getWidget() instanceof HasEnabled) {
			((HasEnabled)getWidget()).setEnabled(isEnabled && enabled);
		}
	}

	

}

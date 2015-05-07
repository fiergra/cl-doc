package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("deprecation")
public abstract class InteractorWidgetLink extends InteractorLink {
	private static final String OBJECT_TYPE = "objectType";
	private static final String FOCUS = "focus";
	public static final String REQUIRED = "required";
	public static final String ENABLED = "enabled";
	protected final Widget widget;
	protected final HashMap<String, String> attributes;
	
	private final boolean isRequired;
	private final boolean isEnabled;
	private final boolean requestFocus;
	private String objectType;

	public InteractorWidgetLink(Interactor interactor, String name, Widget widget, HashMap<String, String> attributes) {
		super(interactor, name);
		this.widget = widget;
		this.attributes = attributes;
		
		isEnabled =  attributes == null || !("false".equals(attributes.get(ENABLED)));
		isRequired = attributes != null && "true".equals(attributes.get(REQUIRED)); 
		requestFocus = attributes != null && "true".equals(attributes.get(FOCUS));
		objectType = attributes != null ? attributes.get(OBJECT_TYPE) : null;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}


	
	protected void hilite(boolean isValid) {
		if (!isValid) {
			getWidget().addStyleName("invalidContent");
		} else {
			getWidget().removeStyleName("invalidContent");
		}
		if (requestFocus) {
			if (getWidget() instanceof Focusable) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
						((Focusable)getWidget()).setFocus(requestFocus);
					}
				});
			} else if (getWidget() instanceof HasFocus) {
				((HasFocus)getWidget()).setFocus(requestFocus);
			}
		}
	}
	
	public Widget getWidget() {
		return widget;
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

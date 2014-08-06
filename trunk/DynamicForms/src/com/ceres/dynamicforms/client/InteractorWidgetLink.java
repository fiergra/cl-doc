package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.Widget;

public abstract class InteractorWidgetLink extends InteractorLink {
	protected final Widget widget;
	protected final HashMap<String, String> attributes;
	
	protected final boolean isRequired;
	protected final boolean focus;

	public InteractorWidgetLink(Interactor interactor, String name, Widget widget, HashMap<String, String> attributes) {
		super(interactor, name);
		this.widget = widget;
		this.attributes = attributes;
		
		isRequired = attributes != null && "true".equals(attributes.get("required")); 
		focus = attributes != null && "true".equals(attributes.get("focus"));
	}

	protected void hilite(boolean isValid) {
		if (!isValid) {
			getWidget().addStyleName("invalidContent");
		} else {
			getWidget().removeStyleName("invalidContent");
		}
		if (focus && getWidget() instanceof Focusable) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					((Focusable)getWidget()).setFocus(focus);
				}
			});
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
			((HasEnabled)getWidget()).setEnabled(enabled);
		}
	}
	
	

}

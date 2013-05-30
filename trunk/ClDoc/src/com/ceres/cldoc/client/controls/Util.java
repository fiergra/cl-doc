package com.ceres.cldoc.client.controls;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Focusable;

public class Util {
	
	public static void setFocus(final Focusable widget) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				widget.setFocus(true);
			}
		});
	}
}

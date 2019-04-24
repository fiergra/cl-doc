package eu.europa.ec.digit.client;

import com.google.gwt.user.client.ui.Label;

import eu.europa.ec.digit.client.i18n.StringResources;

public class StateLabel extends Label {

	public StateLabel(String state, boolean isInitial, boolean isTerminal) {
		setStyleName("stateLabel");
		setText(state != null ? state.substring(0,  1).toUpperCase() : "?");
		setTitle(StringResources.getLabel(state));
		if (isInitial) {
			addStyleDependentName("initial");
		}
		if (isTerminal) {
			addStyleDependentName("terminal");
		}
		
		if (!isInitial && !isTerminal) {
			addStyleDependentName("regular");
		}
	}
	
	

}

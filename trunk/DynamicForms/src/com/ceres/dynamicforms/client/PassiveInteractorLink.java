package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Widget;

public class PassiveInteractorLink extends InteractorLink {

	public PassiveInteractorLink(Interactor interactor, String fieldName, Widget widget, HashMap<String, String> attributes) {
		super(interactor, fieldName, widget, attributes);
	}

	@Override
	public void toDialog(INamedValues item){};
	@Override
	public void fromDialog(INamedValues item){};
}

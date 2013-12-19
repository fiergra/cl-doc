package com.ceres.cldoc.client.views.dynamicforms;

import java.util.HashMap;

import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorLink;
import com.ceres.dynamicforms.client.PassiveInteractorLink;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class PagesFactory implements ILinkFactory {

	@Override
	public InteractorLink createLink(Interactor interactor, String fieldName, final HashMap<String, String> attributes) {
		return new PassiveInteractorLink(interactor, fieldName, 
				new TabLayoutPanel(3,  Unit.EM){

			@Override
			public void add(Widget w) {
				super.add(w, w.getTitle());
			}


		}, attributes) {
		};
	}

}

package com.ceres.cldoc.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClosableTab extends Grid {

	public ClosableTab(final TabLayoutPanel maintab, final Widget tabChild, String labelText) {
		super(1, 2);
		addStyleName("closableTab");
		Image closeButton = new Image("icons/Button-Close-01.png");
		setHeight("12px");
		closeButton.setSize("10px", "10px");
		Label label = new Label(labelText);

		setWidget(0, 0, label);
		setWidget(0, 1, closeButton);
		
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				maintab.remove(tabChild);
				maintab.selectTab(0);
			}
		});
	}

}

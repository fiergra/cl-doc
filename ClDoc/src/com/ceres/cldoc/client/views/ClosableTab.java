package com.ceres.cldoc.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClosableTab extends HorizontalPanel {

	public ClosableTab(final TabLayoutPanel maintab, final Widget tabChild, String labelText) {
		Image closeButton = new Image("icons/Button-Close-01.png");
		setHeight("12px");
		setSpacing(3);
		closeButton.setSize("10px", "10px");
		Label label = new Label(labelText);

		add(closeButton);
		add(label);
		
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				maintab.remove(tabChild);
				maintab.selectTab(0);
			}
		});
	}

}

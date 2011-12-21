package com.ceres.cldoc.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClosableTab extends HorizontalPanel implements IsWidget {

	public ClosableTab(final TabLayoutPanel maintab, final Widget tabChild, String label) {
		Image closeButton = new Image("icons/Button-Close-01.png");
		closeButton.setSize("10px", "10px");
		add(closeButton);
		add(new Label(label));
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				maintab.remove(tabChild);
				maintab.selectTab(0);
			}
		});
	}

}

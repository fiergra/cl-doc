package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class Home extends SplitLayoutPanel {

	public Home(final ClDoc clDoc) {
		HomeScreen homeScreen = new HomeScreen(clDoc);
		DockLayoutPanel shortCuts = new DockLayoutPanel(Unit.PX);
		
		shortCuts.addStyleName("searchResults");
		addEast(shortCuts, 350);
		add(homeScreen);
	}

}

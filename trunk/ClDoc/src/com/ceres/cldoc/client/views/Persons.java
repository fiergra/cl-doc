package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class Persons extends SplitLayoutPanel {

	public Persons(final ClDoc clDoc) {
		PersonSearchTable homeScreen = new PersonSearchTable(clDoc);
		ShortCutsPanel shortCuts = new ShortCutsPanel(clDoc);
		
		shortCuts.addStyleName("searchResults");
		addEast(shortCuts, 350);
		add(homeScreen);
	}

}

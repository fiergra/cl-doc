package com.ceres.cldoc.client;

import com.ceres.cldoc.client.views.ClosableTab;
import com.ceres.cldoc.client.views.ConfiguredTabPanel;
import com.ceres.cldoc.client.views.PersonalFile;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClDoc implements EntryPoint {

	/**
	 * This is the entry point method.
	 */

	private ConfiguredTabPanel<ClDoc> mainTab = new ConfiguredTabPanel<ClDoc>("MAIN", this);
	private Label statusMessage = new Label();
	
	public void onModuleLoad() {
//		mainTab.setStylePrimaryName("mainTab");
		
		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
		Image logo = new Image("dkglogo.png");
		logo.setHeight("60px");
		mainPanel.addNorth(logo, 60);
		mainPanel.addSouth(statusMessage, 20);
		mainPanel.add(mainTab);
		RootLayoutPanel.get().add(mainPanel);
	}
	
	private PersonalFile getPersonalFile(HumanBeing hb) {
		int count = mainTab.getWidgetCount();
		PersonalFile personalFile = null;
		int index = 0;
		
		while (index < count && personalFile == null) {
			Widget tab = mainTab.getWidget(index);
			if (tab instanceof PersonalFile && ((PersonalFile)tab).getHumanBeing().id.equals(hb.id) ) {
				personalFile = (PersonalFile) tab;
			} else {
				index++;
			}
		}
		
		if (personalFile == null) {
			personalFile = new PersonalFile(hb);
		}
		
		return personalFile;
	}
	
	public void openPersonalFile(HumanBeing hb) {
		PersonalFile personalFile = getPersonalFile(hb);
		mainTab.add(personalFile, new ClosableTab(mainTab, personalFile, hb.id + " " + hb.lastName));
		mainTab.selectTab(mainTab.getWidgetIndex(personalFile));
	}
	
	public void status(String text) {
		statusMessage.setText(text);
	}

	public void clearStatus() {
		statusMessage.setText("");
	}
}

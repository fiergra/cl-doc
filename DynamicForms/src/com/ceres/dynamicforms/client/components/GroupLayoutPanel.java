package com.ceres.dynamicforms.client.components;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GroupLayoutPanel extends DockLayoutPanel implements GroupPanel {

	private final HorizontalPanel headerContent = new HorizontalPanel();
	private final Image busyImage = new Image("assets/images/busyblue.gif");
	private Label titleLabel;
	
	public GroupLayoutPanel(String title, Widget content) {
		super(Unit.PX);
		HorizontalPanel header = new HorizontalPanel();
		header.add(headerContent);
		setStyleName("groupLayoutPanel");
		header.setStyleName("headerPanel");
		header.setSize("100%", "42px");
		header.setSpacing(5);
		header.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		headerContent.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titleLabel = new Label(title);
		titleLabel.setStyleName("headerPanelTitle");
		headerContent.add(titleLabel);
		addNorth(header, 42);
		add(content);
		
		HorizontalPanel busyPanel = new HorizontalPanel();
		busyPanel.setSize("100%", "100%");
		busyPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		busyImage.setVisible(false);
		busyPanel.add(busyImage);
		header.add(busyPanel);
	}

	@Override
	public HorizontalPanel getHeader() {
		return headerContent;
	}

	@Override
	public void setBusy(boolean isBusy) {
		busyImage.setVisible(isBusy);
	}
	
	public void setTitleText(String text) {
		titleLabel.setText(text);
	}
}

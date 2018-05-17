package com.ceres.dynamicforms.client.components;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GroupLayoutPanel extends DockLayoutPanel implements GroupPanel {

	private final EnabledHorizontalPanel header = new EnabledHorizontalPanel();
	private final EnabledHorizontalPanel headerContent = new EnabledHorizontalPanel();
	private final Image busyImage = new Image("assets/images/busyblue.gif");
	private Label titleLabel;
	private boolean enabled;
	private Widget content;
	
	public GroupLayoutPanel(String title, Widget content) {
		super(Unit.PX);
		this.content = content;
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

	public void removeContent() {
		if (this.content != null) {
			remove(this.content);
		} 
	}
	

	public void setContent(Widget content) {
		removeContent();
		this.content = content;
		add(content);
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

	public String getTitleText() {
		return titleLabel.getText();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (enabled != this.enabled) {
			header.setEnabled(enabled);
			if (content instanceof HasEnabled) {
				((HasEnabled)content).setEnabled(enabled);
			}
			this.enabled = enabled;
		}
	}
}

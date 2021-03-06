package com.ceres.cldoc.client.controls;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.PushButton;

public class LinkButton extends PushButton {
	
	private final String disabledImageUrl;
	private final String enabledImageUrl;
	private boolean enabled = true;

	public LinkButton(String toolTip, String enabledImageUrl, String disabledImageUrl, final ClickHandler clickHandler) {
		super(enabledImageUrl);
		this.disabledImageUrl = disabledImageUrl;
		this.enabledImageUrl = enabledImageUrl;
		addStyleName("linkButton");
		if (clickHandler != null) {
			addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (enabled) {
						clickHandler.onClick(event);
					}
				}
			});
		}
		setTitle(toolTip);
	}
	
	@Override
	public HandlerRegistration addClickHandler(final ClickHandler ch) {
		return super.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (enabled) {
					ch.onClick(event);
				}
			}
		});
	}
	
	public void enable(boolean enabled) {
		super.setEnabled(enabled);
		this.enabled = enabled;
		setHTML("<img src=\"" + (enabled ? enabledImageUrl : disabledImageUrl) + "\"/>");
//		setUrl(enabled ? enabledImageUrl : disabledImageUrl);
	}

}

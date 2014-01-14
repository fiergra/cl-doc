package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public abstract class DurationLink extends InteractorLink {

	private DateLink fromLink;
	private DateLink toLink;

	public DurationLink(final Interactor interactor, DateLink fromLink, DateLink toLink) {
		super(interactor, "duration:" + fromLink.getName() + "-" + toLink.getName());
		this.fromLink = fromLink;
		this.toLink = toLink;
		
		fromLink.getWidget().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(DurationLink.this);
			}
		});

		toLink.getWidget().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(DurationLink.this);
			}
		});

	}

	@Override
	public void toDialog(Map<String, Serializable> item) {

	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {

	}
	

	@Override
	public boolean isValid() {
		boolean isValid = true;
		
		if (isValid && !fromLink.isEmpty() && !toLink.isEmpty()) {
			isValid = fromLink.getWidget().getDate().getTime() <= toLink.getWidget().getDate().getTime();
		}
		
		return isValid;
	}

	@Override
	public boolean isEmpty() {
		return fromLink.isEmpty() && toLink.isEmpty();
	}

	@Override
	protected void hilite(boolean isValid) {
		fromLink.hilite(isValid);
		toLink.hilite(isValid);
	}
	
	

}

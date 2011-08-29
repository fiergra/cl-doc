package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class ValueBagSummaryRenderer extends FocusPanel {

	private ValueBag valueBag;

	public ValueBagSummaryRenderer(ValueBag valueBag, ClickHandler clickHandler) {
		this.valueBag = valueBag;
		setup();
		setStyleName("valueBagSummaryRenderer");
		addClickHandler(clickHandler);
	}

	private void setup() {
		setWidth("100%");
		HorizontalPanel line = new HorizontalPanel();
		line.setWidth("100%");
		
		Label lId = new Label(valueBag.getId().toString());
		line.add(lId);
		Label l = new Label(valueBag.getCanonicalName());
		line.add(l);
		
		add(line);
	}

}

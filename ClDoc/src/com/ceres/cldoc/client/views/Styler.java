package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Styler extends VerticalPanel {
	private ClDoc clDoc;

	public Styler(ClDoc clDoc) {
		this.clDoc = clDoc;
		setup();
	}

	final TextBox attributeBox = new TextBox();
	final TextBox valueBox = new TextBox();
	final Button button = new Button("push");
	final HTML results = new HTML("asdf!");

	private void setup() {
		
		add(new Label("attribute"));
		add(attributeBox);
		add(new Label("value"));
		add(valueBox);
		add(button);
		add(results);
		
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSetStyle();
			}
		});
	}


	protected void doSetStyle() {
		DOM.setStyleAttribute(results.getElement(), attributeBox.getText(), valueBox.getText());
	}
	

}

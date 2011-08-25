package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class PersonRenderer extends FocusPanel {
	private ValueBag valueBag;
	private OnClick onClickEdit;
	private OnClick onClickOpen;
	private HumanBeing person;

	public PersonRenderer(ValueBag p, OnClick onClickOpen, OnClick onClickEdit) {
		this.valueBag = p;
		this.onClickOpen = onClickOpen;
		this.onClickEdit = onClickEdit;
		setup();
	}

	public PersonRenderer(HumanBeing p, OnClick onClickOpen, OnClick onClickEdit) {
		this.person = p;
		this.onClickOpen = onClickOpen;
		this.onClickEdit = onClickEdit;
		setup();
	}

	private void setup() {
		setStylePrimaryName("personRenderer");
		Label lFirstName = new Label(getFirstName());
		HTML lLastName = new HTML("<b>" + getLastName() + "</b>");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(lLastName);
		hp.add(lFirstName);
		
		add(hp);
		
		addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				onClickOpen.onClick(null);
			}
		});
		
		Button editButton = new Button("...");
		editButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				onClickEdit.onClick(null);
			}
		});
		hp.add(editButton);
	}

	private String getLastName() {
		return person != null ? person.lastName : valueBag.getString("lastName");
	}

	private String getFirstName() {
		return person != null ? person.firstName : valueBag.getString("firstName");
	}
}

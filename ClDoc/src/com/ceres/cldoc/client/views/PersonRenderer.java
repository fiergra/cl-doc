package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IGenericItem;
import com.ceres.cldoc.model.Person;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class PersonRenderer extends FocusPanel {
	private IGenericItem valueBag;
	private OnClick<Person> onClickEdit;
	private OnClick<Person> onClickOpen;
	private Person person;

//	public PersonRenderer(GenericItem p, OnClick<HumanBeing> onClickOpen, OnClick<HumanBeing> onClickEdit) {
//		this.valueBag = p;
//		this.onClickOpen = onClickOpen;
//		this.onClickEdit = onClickEdit;
//		setup();
//	}

	public PersonRenderer(Person p, OnClick<Person> onClickOpen, OnClick<Person> onClickEdit) {
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
				onClickOpen.onClick(person);
			}
		});
		
		Button editButton = new Button("...");
		editButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				onClickEdit.onClick(person);
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

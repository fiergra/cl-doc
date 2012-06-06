package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.Person;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class PersonRenderer extends FocusPanel {
	private IAct act;
	private final OnClick onClickEdit;
	private final OnClick onClickOpen;
	private final Person person;

//	public PersonRenderer(GenericAct p, OnClick<HumanBeing> onClickOpen, OnClick<HumanBeing> onClickEdit) {
//		this.act = p;
//		this.onClickOpen = onClickOpen;
//		this.onClickEdit = onClickEdit;
//		setup();
//	}

	public PersonRenderer(Person p, OnClick onClickOpen, OnClick onClickEdit) {
		this.person = p;
		this.onClickOpen = onClickOpen;
		this.onClickEdit = onClickEdit;
		setup();
	}

	private void setup() {
		DateTimeFormat f = DateTimeFormat.getFormat("dd.MM.yyyy");
		setStylePrimaryName("personRenderer");
		Grid grid = new Grid(1, 5);
//		grid.setWidth("100%");
		Label id = new Label(String.valueOf(person.id));
		id.setWidth("4em");
		grid.setWidget(0, 0, id);
		HTML lastName = new HTML("<b>" + getLastName() + "</b>");
		lastName.setWidth("10em");
		grid.setWidget(0, 1, lastName);
		Label firstName = new Label(getFirstName());
		firstName.setWidth("10em");
		grid.setWidget(0, 2, firstName);
		grid.setWidget(0, 3, new Label(person.dateOfBirth != null ? ("*" + f.format(person.dateOfBirth)) : "" ));
		
//		Label lId = new Label(getFirstName());
//		Label lFirstName = new Label(getFirstName());
//		HTML lLastName = new HTML("<b>" + getLastName() + "</b>");
//
//		HorizontalPanel hp = new HorizontalPanel();
//		hp.setSpacing(3);
//		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		hp.add(lLastName);
//		hp.add(lFirstName);
//		
		add(grid);
		
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
		grid.setWidget(0, 4, editButton);
	}

	private String getLastName() {
		return person != null ? person.lastName : act.getString("lastName");
	}

	private String getFirstName() {
		return person != null ? person.firstName : act.getString("firstName");
	}
}

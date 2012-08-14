package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Person;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PersonSearchTable extends ClickableTable<Person> {
	protected ClDoc clDoc;

	public PersonSearchTable(final ClDoc clDoc) {
		super(clDoc);
		final TextBox searchBox = new TextBox();
		setListRetrieval(new ListRetrievalService<Person>() {
			@Override
			public void retrieve(String filter, AsyncCallback<List<Person>> callback) {
				SRV.humanBeingService.search(clDoc.getSession(), searchBox.getText(), callback);
			}
		});
		setOnClick(new OnClick<Person>() {

			@Override
			public void onClick(Person person) {
				SRV.humanBeingService.findById(clDoc.getSession(), person.id, new DefaultCallback<Person>(clDoc, "findById") {

					@Override
					public void onSuccess(Person result) {
						clDoc.openEntityFile(result, new PersonalFileHeader(result), "CLDOC.PERSONALFILE");
					}
				});
			}
		});
		this.clDoc = clDoc;
		
		HorizontalPanel hp = new HorizontalPanel();
		final Timer timer = new Timer() {

			@Override
			public void run() {
				refresh();
			}
		};
		searchBox.setWidth("50em");
		searchBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				timer.cancel();
				timer.schedule(250);
			}
		});

		hp.setSpacing(2);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(new Label(SRV.c.search()));
		hp.add(searchBox);

		Image pbNew = addButton(SRV.c.newPPP(), "icons/32/Person-New-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PersonEditor.editPerson(clDoc, new Person());
			}
		});
		pbNew.setPixelSize(32, 32);
		hp.add(pbNew);
		
		
		addWidget(hp);
		getColumnFormatter().addStyleName(4, "hundertPercentWidth");

	}

	
/*
	private static void loadAndOpenFile(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onSuccess(Person result) {
				clDoc.openEntityFile(clDoc.getSession(), result, new PersonalFileHeader(result), "CLDOC.PERSONALFILE");
			}
		});
	}
	
	private static void loadAndEditPerson(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onSuccess(Person result) {
				editPerson(clDoc, result);
			}
		});
	}
	
*/	

	@Override
	public void addRow(FlexTable grid, int row, Person person) {
		DateTimeFormat f = DateTimeFormat.getFormat("dd.MM.yyyy");
		Label id = new Label(String.valueOf(person.id));
		
		grid.setWidget(row, 0, id);
		HTML lastName = new HTML("<b>" + person.lastName + "</b>");
		lastName.setWidth("10em");
		grid.setWidget(row, 1, lastName);
		Label firstName = new Label(person.firstName);
		firstName.setWidth("10em");
		grid.setWidget(row, 2, firstName);
		grid.setWidget(row, 3, new Label(person.dateOfBirth != null ? ("*" + f.format(person.dateOfBirth)) : "" ));
		
		if (person.gender != null) {
			Image gender = person.gender.id.equals(152l) ? new Image("icons/male-sign.png") : new Image("icons/female-sign.png");
			gender.setHeight("1em");
			grid.setWidget(row, 4, gender);
		} else {
			grid.setWidget(row, 4, new Label());
		}
		
	}

}

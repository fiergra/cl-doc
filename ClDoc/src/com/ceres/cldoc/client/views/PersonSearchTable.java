package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PersonWrapper;
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
						clDoc.openPersonalFile(clDoc.getSession(), result);
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

		Image pbNew = addButton(SRV.c.newPPP(), "");
		hp.add(pbNew);
		pbNew.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				editPerson(clDoc, new Person());
			}
		});
		
		
		addWidget(hp);
	}

	
	private static void savePerson(ClDoc clDoc, Person result) {
		SRV.humanBeingService.save(clDoc.getSession(), result, new DefaultCallback<Person>(clDoc, "save") {

			@Override
			public void onSuccess(Person result) {
//				searchBox.setText(result.lastName);
//				doSearch();
			}
		});
		
	}
	
	private static void deletePerson(ClDoc clDoc, Person person) {
		SRV.humanBeingService.delete(clDoc.getSession(), person, new DefaultCallback<Void>(clDoc, "deletePerson"){

			@Override
			public void onSuccess(Void result) {
				
			}});
	}

	private static void loadAndOpenFile(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onSuccess(Person result) {
				clDoc.openPersonalFile(clDoc.getSession(), result);
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
	
	
	private static void editPerson(final ClDoc clDoc, final Person humanBeing) {
		final PersonEditor pe = new PersonEditor(clDoc, new PersonWrapper(humanBeing));
		pe.showModal("PersonEditor", 
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				savePerson(clDoc, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
				deletePerson(clDoc, humanBeing);
			}
		},
		new OnClick<PersonWrapper>() {
			
			@Override
			public void onClick(PersonWrapper v) {
				pe.close();
			}
		}
		);
	}

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
	}

}

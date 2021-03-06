package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Patient;
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
			public void onClick(final Person person) {
				if (person instanceof Patient) {
					findAndOpen((Patient)person);
				} else {
					new MessageBox("Neue Patientenakte", "Wollen Sie eine Patientenakte fuer " + person.getName() + " anlegen?", MessageBox.MB_YESNO, MESSAGE_ICONS.MB_ICON_QUESTION){

						@Override
						protected void onClick(int result) {
							if (result == MB_YES) {
								PersonEditor.editPerson(clDoc, new Patient(person), new OnClick<Person>(){

									@Override
									public void onClick(Person person) {
										findAndOpen((Patient) person);
									}});
							}
						}}.show();
				}
			}
		});
		this.clDoc = clDoc;
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(5);
		final Timer timer = new Timer() {

			@Override
			public void run() {
				refresh();
			}
		};
		searchBox.setWidth("200px");
		searchBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				timer.cancel();
				timer.schedule(250);
			}
		});

//		hp.setSpacing(2);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//		hp.add(new Label(SRV.c.search()));
//		hp.add(searchBox);
		addWidget(new Label(SRV.c.search()));
		addWidget(searchBox);

		Image pbNew = addButton(SRV.c.newPPP(), "icons/32/Person-New-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PersonEditor.editPerson(clDoc, new Person(), new OnClick<Person>(){

					@Override
					public void onClick(Person person) {
						searchBox.setText(person.getName());
					}});

			}
		});
		pbNew.setPixelSize(18,18);
		hp.add(pbNew);
		
		
		addWidget(hp);
		getColumnFormatter().addStyleName(4, "hundertPercentWidth");

	}

	
/*
	private static void loadAndOpenFile(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onResult(Person result) {
				clDoc.openEntityFile(clDoc.getSession(), result, new PersonalFileHeader(result), "CLDOC.PERSONALFILE");
			}
		});
	}
	
	private static void loadAndEditPerson(final ClDoc clDoc, long pid) {
		SRV.humanBeingService.findById(clDoc.getSession(), pid, new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onResult(Person result) {
				editPerson(clDoc, result);
			}
		});
	}
	
*/	

	protected void findAndOpen(Patient person) {
		SRV.humanBeingService.findById(clDoc.getSession(), person.getId(), new DefaultCallback<Person>(clDoc, "findById") {

			@Override
			public void onResult(Person result) {
				clDoc.openEntityFile(result, new PersonalFileHeader(result), "CLDOC.PERSONALFILE");
			}
		});
		
	}


	@Override
	public boolean addRow(FlexTable grid, int row, Person person) {
		DateTimeFormat f = DateTimeFormat.getFormat("dd.MM.yyyy");
		Label id = new Label(
				person instanceof Patient ? 
						String.valueOf(person.getDisplayId()) : "-");
		id.addStyleName("resultCell");
		grid.setWidget(row, 0, id);
		HTML lastName = new HTML("<b>" + person.lastName + "</b>");
		lastName.setWidth("10em");
		lastName.addStyleName("resultCell");
		grid.setWidget(row, 1, lastName);
		Label firstName = new Label(person.firstName);
		firstName.setWidth("10em");
		firstName.addStyleName("resultCell");
		grid.setWidget(row, 2, firstName);
		Label dob = new Label(person.dateOfBirth != null ? ("*" + f.format(person.dateOfBirth)) : "" );
		dob.addStyleName("resultCell");
		grid.setWidget(row, 3, dob);
		
		if (person.gender != null) {
			Image gender = person.gender.id.equals(152l) ? new Image("icons/male-sign.png") : new Image("icons/female-sign.png");
			gender.setHeight("1em");
			grid.setWidget(row, 4, gender);
		} else {
			grid.setWidget(row, 4, new Label());
		}
		return true;
		
	}

}

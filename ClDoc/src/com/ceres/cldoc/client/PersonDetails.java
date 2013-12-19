package com.ceres.cldoc.client;

import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.model.Patient;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PatientWrapper;
import com.ceres.cldoc.shared.domain.PersonWrapper;
import com.ceres.core.IApplication;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.WidgetCreator;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PersonDetails extends DockLayoutPanel {
	private LinkButton pbSave;
	private final IApplication clDoc;
	private final PersonWrapper personWrapper;
	private final Interactor ia;

	public PersonDetails(IApplication clDoc, Person person) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.personWrapper = person instanceof Patient ? new PatientWrapper((Patient) person) : new PersonWrapper(person);
		this.ia = new Interactor();

		Widget content = WidgetCreator.createWidget(
				"<Form>" +
				((person instanceof Patient) ? "<FormItem label=\"ID\"><long fieldName=\"perId\" required=\"true\"/></FormItem>" : "") +
				"<FormItem label=\"Name\"><HBox><ItemFieldTextInput required=\"true\" fieldName=\"lastName\"/><Label text=\"Vorname\"/><ItemFieldTextInput required=\"true\" fieldName=\"firstName\"/><Label text=\"Geburtsdatum\"/><ItemFieldDateField required=\"true\" fieldName=\"dateOfBirth\"/><Label text=\"Geschlecht\"/><option parent=\"MASTERDATA.GENDER\" fieldName=\"gender\"/></HBox></FormItem><FormItem label=\"Strasse\"><HBox><ItemFieldTextInput fieldName=\"primaryAddress.street\"/><Label text=\"Nr.\"/><ItemFieldTextInput fieldName=\"primaryAddress.number\"/></HBox></FormItem><FormItem label=\"PLZ\"><HBox><ItemFieldTextInput fieldName=\"primaryAddress.postCode\"/><Label text=\"Ort\"/><ItemFieldTextInput width=\"100%\" fieldName=\"primaryAddress.city\"/></HBox></FormItem><FormItem label=\"Telefon\"><HBox><ItemFieldTextInput fieldName=\"primaryAddress.phone\"/><Label text=\"c/o\"/><ItemFieldTextInput width=\"100%\" fieldName=\"primaryAddress.co\"/></HBox></FormItem><FormItem label=\"Bemerkung\"><ItemFieldTextArea width=\"100%\" fieldName=\"primaryAddress.note\"/></FormItem></Form>", ia);

		// this.personEditor = new PersonEditor(clDoc, humanBeing, new
		// Runnable() {
		//
		// @Override
		// public void run() {
		// setModified();
		// }
		// }, null);
		addNorth(createButtons(), 3);
		content.setWidth("100%");
		add(content);
		ia.setChangeHandler(new Runnable() {
			
			@Override
			public void run() {
				pbSave.enable(ia.isValid() && ia.isModified());
			}
		});
		ia.toDialog(personWrapper);
	}

	private Widget createButtons() {
		HorizontalPanel buttonContainer = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttonContainer.setWidth("100%");
		buttonContainer.addStyleName("buttonsPanel");
		buttonContainer
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonContainer.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonContainer.add(buttons);

		pbSave = new LinkButton("Speichern", "icons/32/Save-icon.png",
				"icons/32/Save-icon.disabled.png", new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						ia.fromDialog(personWrapper);
						SRV.humanBeingService.save(clDoc.getSession(),
								personWrapper.unwrap(),
								new DefaultCallback<Person>(clDoc, "save") {

									@Override
									public void onSuccess(Person result) {
										pbSave.enable(false);
									}
								});
					}
				});
		pbSave.enable(false);
		buttons.add(pbSave);

		return buttonContainer;
	}

	protected void setModified() {
		pbSave.enable(true);
	}

}

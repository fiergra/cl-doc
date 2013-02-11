package com.ceres.cldoc.client;

import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.PersonEditor;
import com.ceres.cldoc.model.Person;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class PersonDetails extends DockLayoutPanel {
	private LinkButton pbSave;
	private final PersonEditor personEditor;
	private final Person humanBeing;
	private final ClDoc clDoc;
	
	public PersonDetails(ClDoc clDoc, Person humanBeing) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.humanBeing = humanBeing;
		this.personEditor = new PersonEditor(clDoc, humanBeing, new Runnable() {
			
			@Override
			public void run() {
				setModified();
			}
		}, null);
		addNorth(createButtons(), 3);
		personEditor.setWidth("100%");
		add(new ScrollPanel(personEditor));
	}

	
	private Widget createButtons() {
		HorizontalPanel buttonContainer = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		buttonContainer.setWidth("100%");
		buttonContainer.addStyleName("buttonsPanel");
		buttonContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonContainer.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonContainer.add(buttons);
		
		pbSave = new LinkButton("Speichern", "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				personEditor.fromDialog();
				SRV.humanBeingService.save(clDoc.getSession(), humanBeing, new DefaultCallback<Person>(clDoc, "save") {

					@Override
					public void onSuccess(Person result) {
						pbSave.enable(false);
						personEditor.clearModification();
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

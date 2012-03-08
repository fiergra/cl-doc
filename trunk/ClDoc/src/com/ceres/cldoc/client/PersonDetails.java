package com.ceres.cldoc.client;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.PersonEditor;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.shared.domain.PersonWrapper;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PersonDetails extends DockLayoutPanel {
	private Image pbSave = new Image("icons/32/Save-icon.png");
	private PersonEditor personEditor;
	private Person humanBeing;
	private ClDoc clDoc;
	
	public PersonDetails(ClDoc clDoc, Person humanBeing) {
		super(Unit.PX);
		this.clDoc = clDoc;
		this.humanBeing = humanBeing;
		this.personEditor = new PersonEditor(clDoc, new PersonWrapper(humanBeing), new Runnable() {
			
			@Override
			public void run() {
				setModified();
			}
		});
		addNorth(createButtons(), 32);
		HorizontalPanel container = new HorizontalPanel();
		container.add(personEditor);
		add(container);
	}

	
	private Widget createButtons() {
		HorizontalPanel buttonContainer = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		
		buttonContainer.setWidth("100%");
		buttonContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonContainer.add(buttons);
		
		pbSave.setVisible(false);
		pbSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				personEditor.fromDialog();
				SRV.humanBeingService.save(clDoc.getSession(), humanBeing, new DefaultCallback<Person>(clDoc, "save") {

					@Override
					public void onSuccess(Person result) {
						pbSave.setVisible(false);
						personEditor.clearModification();
					}
				});
			}
		});

		buttons.add(pbSave);
		
		return buttonContainer;
	}

	protected void setModified() {
		pbSave.setVisible(true);
	}

}

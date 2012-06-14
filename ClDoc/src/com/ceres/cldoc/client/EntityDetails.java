package com.ceres.cldoc.client;

import com.ceres.cldoc.model.Entity;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class EntityDetails extends DockLayoutPanel {
	private final Image pbSave = new Image("icons/32/Save-icon.png");
	private final ClDoc clDoc;
	private final Entity entity;
	
	public EntityDetails(ClDoc clDoc, Entity entity) {
		super(Unit.PX);
		this.clDoc = clDoc;
		this.entity = entity;
		addNorth(createButtons(), 32);
//		HorizontalPanel container = new HorizontalPanel();
//		container.add(personEditor);
//		add(container);
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
//				personEditor.fromDialog();
//				SRV.humanBeingService.save(clDoc.getSession(), humanBeing, new DefaultCallback<Person>(clDoc, "save") {
//
//					@Override
//					public void onSuccess(Person result) {
//						pbSave.setVisible(false);
//						personEditor.clearModification();
//					}
//				});
			}
		});

		buttons.add(pbSave);
		
		return buttonContainer;
	}

	protected void setModified() {
		pbSave.setVisible(true);
	}

}

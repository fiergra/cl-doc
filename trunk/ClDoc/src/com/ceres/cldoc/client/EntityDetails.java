package com.ceres.cldoc.client;

import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.Form;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
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

	public EntityDetails(final ClDoc clDoc, final Entity entity) {
		super(Unit.PX);
		this.clDoc = clDoc;
		this.entity = entity;
		addNorth(createButtons(), 32);
		final Form<IAct> masterDataForm = new Form<IAct>(clDoc, null,
				new Runnable() {

					@Override
					public void run() {
						pbSave.setVisible(true);
					}
				}) {

			@Override
			protected void setup() {

			}
		};

		SRV.catalogService.getCatalog(clDoc.getSession(), entity.type,
				new DefaultCallback<Catalog>(clDoc, "getEntityType") {

					@Override
					public void onSuccess(final Catalog catalog) {

						SRV.actService.findByEntity(clDoc.getSession(), entity, Participation.MASTERDATA,
								new DefaultCallback<List<Act>>(clDoc,
										"loadMasterData") {

									private Act act = null;

									@Override
									public void onSuccess(List<Act> result) {
										Iterator<Act> iter = result.iterator();
										while (iter.hasNext() && act == null) {
											Act next = iter.next();
											if (next.className
													.equals(catalog.code)) {
												act = next;
											}
										}
										if (act != null) {
											SRV.configurationService.getLayoutDefinition(
													clDoc.getSession(),
													catalog.code,
													LayoutDefinition.MASTER_DATA_LAYOUT,
													new DefaultCallback<LayoutDefinition>(
															clDoc,
															"getMasterDataLayout") {

														@Override
														public void onSuccess(
																LayoutDefinition layout) {
															masterDataForm
																	.setModel(act);
															masterDataForm
																	.parseAndCreate(layout.xmlLayout);
														}
													});
										}
									}
								});

					}
				});
		HorizontalPanel formContainer = new HorizontalPanel();
		formContainer.addStyleName("formContainer");
		formContainer.setSize("100%", "100%");
		masterDataForm.setWidth("100%");
		formContainer.add(masterDataForm);
		add(formContainer);

	}

	private Widget createButtons() {
		HorizontalPanel buttonContainer = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		
		buttonContainer.setWidth("100%");
		buttonContainer
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonContainer.add(buttons);
		buttonContainer.setStylePrimaryName("buttonsPanel");
		
		pbSave.setVisible(false);
		pbSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// personEditor.fromDialog();
				// SRV.humanBeingService.save(clDoc.getSession(), humanBeing,
				// new DefaultCallback<Person>(clDoc, "save") {
				//
				// @Override
				// public void onSuccess(Person result) {
				// pbSave.setVisible(false);
				// personEditor.clearModification();
				// }
				// });
			}
		});

		buttons.add(pbSave);

		return buttonContainer;
	}

	protected void setModified() {
		pbSave.setVisible(true);
	}

}

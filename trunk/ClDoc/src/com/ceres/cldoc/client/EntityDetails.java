package com.ceres.cldoc.client;


public class EntityDetails {}/*extends DockLayoutPanel {
	private final Image pbSave = new Image("icons/32/Save-icon.png");²
	private final ClDoc clDoc;
	private final Entity entity;
	private final Form<IAct> masterDataForm;
	
	public EntityDetails(final ClDoc clDoc, final Entity entity) {
		super(Unit.PX);
		this.clDoc = clDoc;
		this.entity = entity;
		addNorth(createButtons(), 34);
		masterDataForm = new Form<IAct>(clDoc, null,
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

						SRV.actService.findByEntity(clDoc.getSession(), entity, Participation.MASTERDATA.id,
								new DefaultCallback<List<Act>>(clDoc,
										"loadMasterData") {

									private Act masterData;
									
									@Override
									public void onSuccess(List<Act> result) {
										Iterator<Act> iter = result.iterator();
										while (iter.hasNext() && masterData == null) {
											Act next = iter.next();
											if (next.actClass.name
													.equals(catalog.code)) {
												masterData = next;
											}
										}
										if (masterData != null) {
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
																	.setModel(masterData);
															masterDataForm
																	.parseAndCreate(layout.xmlLayout);
															masterDataForm.toDialog();
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
				masterDataForm.fromDialog();
				SRV.actService.save(clDoc.getSession(), (Act)masterDataForm.getModel(), new DefaultCallback<Act>(clDoc,"saveMasterData") {

					@Override
					public void onSuccess(Act result) {
						masterDataForm.clearModification();
						masterDataForm.setModel(result);
						pbSave.setVisible(false);
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

}*/

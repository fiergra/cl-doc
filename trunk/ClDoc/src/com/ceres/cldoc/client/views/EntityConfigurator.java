package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ClickableTree;
import com.ceres.cldoc.client.controls.ERTree;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class EntityConfigurator extends DockLayoutPanel {

//	private final Image pbSave = new Image("icons/32/Save-icon.png");
//	private final Image pbNew = new Image("icons/32/File-New-icon.png");
//	private final Image pbDelete = new Image("icons/32/File-Delete-icon.png");
//	private final Image pbUpload = new Image("icons/32/Button-Upload-icon.png");
//	private final Image pbDownload = new Image("icons/32/Button-Download-icon.png");
//
	
	public EntityConfigurator(ClDoc clDoc) {
		super(Unit.PX);
		setup(clDoc);
	}

	private void setup(final ClDoc clDoc) {
		DockLayoutPanel listPanel = new DockLayoutPanel(Unit.EM);
		DockLayoutPanel treePanel = new DockLayoutPanel(Unit.EM);
		final ListBox cmbTypes = new ListBox();
		final ArrayList<Catalog> entityTypes = new ArrayList<Catalog>();
		final ClickableTable<Entity> entityTable;
		
		final ERTree etree = new ERTree(clDoc, 
				new OnClick<EntityRelation>() {

			@Override
			public void onClick(EntityRelation pp) {
				ClickableTree<EntityRelation> tree = new ClickableTree<EntityRelation>(clDoc, true) {};
				ClickableTable<Entity> table = new ClickableTable<Entity>(clDoc) {

					@Override
					public void addRow(FlexTable table, int row, Entity entry) {
						table.setWidget(row, 0, new Label(entry.name));
					}
				};
				addWest(tree, 300);
				add(table);

			}
		}, 
		new OnClick<EntityRelation>() {

			@Override
			public void onClick(EntityRelation pp) {
				System.out.println(pp.id);
			}
		}, 
		true);

		cmbTypes.setVisibleItemCount(1);
		ListRetrievalService<Entity> listRetrieval = new ListRetrievalService<Entity>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Entity>> callback) {
				int selectedTypeIndex = cmbTypes.getSelectedIndex();
				Long typeId = entityTypes.get(selectedTypeIndex).id;
				SRV.entityService.list(clDoc.getSession(), typeId.intValue(), callback);
			}
		};
		OnClick<Entity> onClick = new OnClick<Entity>() {
			
			@Override
			public void onClick(Entity entity) {
				etree.setEntity(entity);
			}
		};
		entityTable = new ClickableTable<Entity>(clDoc, listRetrieval, onClick, true) {
			
			@Override
			public void addRow(FlexTable table, int row, Entity entry) {
				table.setWidget(row, 0, new Label(entry.id.toString()));
				Label label = new Label(entry.name);
				clDoc.getDragController().makeDraggable(label, new Label(entry.name));
				table.setWidget(row, 1, label);
			}
		};
		
		listPanel.addNorth(cmbTypes, 2);
		listPanel.add(entityTable);
		
		entityTable.insertButton("new", "icons/32/File-New-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				int selectedTypeIndex = cmbTypes.getSelectedIndex();
				final Catalog type = entityTypes.get(selectedTypeIndex);
				final String code = type.code;

				SRV.configurationService.getLayoutDefinition(clDoc.getSession(), code, LayoutDefinition.MASTER_DATA_LAYOUT, new DefaultCallback<LayoutDefinition>(clDoc, "loadLayout") {

					@Override
					public void onSuccess(LayoutDefinition result) {
						final Act model = new Act(code);
						final Form form = new Form<IAct>(clDoc, model, null) {

							@Override
							protected void setup() {
							}
						};
						
						if (result != null) {
							final TextBox txtName = new TextBox();
							form.addLine("Name", txtName);
							form.parseAndCreate(result.xmlLayout, false);
							form.showModal("Neu", 
								new OnClick<Act>() {

									@Override
									public void onClick(Act pp) {
										Entity entity = new Entity();
										entity.name = txtName.getText();
										entity.type = type.id.intValue();
										SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "saveEntity") {

											@Override
											public void onSuccess(Entity result) {
												SRV.actService.save(clDoc.getSession(), model, new DefaultCallback<Act>(clDoc, "") {

													@Override
													public void onSuccess(
															Act result) {
														entityTable.refresh();
													}
												});
											}
										});
									}
								}, 
								null, 
								new OnClick<Void>() {

								@Override
								public void onClick(Void pp) {
//									form.close();
								}
							});
						} else {
							new MessageBox("Fehlende Konfiguration", "Kein Layout fuer '" + code + "' definiert.", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_INFO).show();
						}
					}
				});
				
			}
		});
		SRV.configurationService.listCatalogs(clDoc.getSession(), "MASTERDATA.EntityTypes", new DefaultCallback<List<Catalog>>(clDoc, "list entity types") {

			@Override
			public void onSuccess(List<Catalog> result) {
				entityTypes.clear();
				entityTypes.addAll(result);
				
//				cmbTypes.addItem("---", (String)null);
				for (Iterator<Catalog> iterator = result.iterator(); iterator.hasNext();) {
					Catalog catalog = iterator.next();
				
					cmbTypes.addItem(catalog.text, String.valueOf(catalog.id));
				}
			}
		});
		
		cmbTypes.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				String value = cmbTypes.getValue(cmbTypes.getSelectedIndex());
				if (value != null) {
					entityTable.refresh();
				}
			}
		});
		
		final Label selectionLabel = new Label("-");
		selectionLabel.setWidth("200px");
		SplitLayoutPanel mainSplit = new SplitLayoutPanel();
		mainSplit.addWest(listPanel, 300);
		
		HorizontalPanel treeButtons = new HorizontalPanel();
		final RadioButton rbSubject = new RadioButton("relDirection", "asSubject");
		final RadioButton rbObject = new RadioButton("relDirection", "asObject");
		treeButtons.add(rbObject);
		rbObject.setValue(true);
		ClickHandler clickHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				etree.setDirection(rbSubject.getValue());
			}
		};
		rbObject.addClickHandler(clickHandler);
		rbSubject.addClickHandler(clickHandler);
		treeButtons.add(rbSubject);
		
		treePanel.addNorth(treeButtons, 2);
		treePanel.add(etree);
		mainSplit.add(treePanel);
		add(mainSplit);

		
	}

}

package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ERTree;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

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
//				ClickableTree<EntityRelation> tree = new ClickableTree<EntityRelation>(clDoc, true) {};
//				ClickableTable<Entity> table = new ClickableTable<Entity>(clDoc) {
//
//					@Override
//					public void addRow(FlexTable table, int row, Entity entry) {
//						table.setWidget(row, 0, new Label(entry.name));
//					}
//				};
//				addWest(tree, 300);
//				add(table);

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
			public boolean addRow(FlexTable table, int row, final Entity entry) {
				table.setWidget(row, 0, new Label(entry.id.toString()));
				Label label = new Label(entry.getName());
//				clDoc.getDragController().makeDraggable(label, new Label(entry.name));
				table.setWidget(row, 1, label);
				table.getColumnFormatter().addStyleName(1, "hundertPercentWidth");
				Image pbEdit = new Image("icons/16/Edit-Document-icon.png");
				pbEdit.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						SRV.actService.findByEntity(clDoc.getSession(), entry, Participation.PROTAGONIST.id, new DefaultCallback<List<Act>>(clDoc, "loadMasterData") {

							@Override
							public void onSuccess(List<Act> result) {
								Catalog type = getSelectedType(cmbTypes, entityTypes);
								Act act = null;
								Iterator<Act> iter = result.iterator();
								while (iter.hasNext() && act == null) {
									Act next = iter.next();
									if (next.actClass.name.equals(type.code)) {
										act = next;
									}
								}
								if (act != null) {
									editEntityMasterData(clDoc, null, act, entry);
								} else {
									createMasterData(clDoc, null, type, entry);
								}
							}
						});
					}
				});
				Image pbDelete = new Image("icons/16/File-Delete-icon.png");
				pbDelete.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						SRV.entityService.delete(clDoc.getSession(), entry, new DefaultCallback<Void>(clDoc, "deleteEntity") {

							@Override
							public void onSuccess(Void result) {
//								entityTable.refresh();
							}
						});
					}
				});
				table.setWidget(row, 2, pbEdit);
				table.setWidget(row, 3, pbDelete);
				return true;
			}
		};
		
		listPanel.addNorth(cmbTypes, 2);
		listPanel.add(entityTable);
		
		entityTable.insertButton("new", "icons/32/File-New-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createMasterData(clDoc, entityTable, getSelectedType(cmbTypes, entityTypes), null);
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

	protected Catalog getSelectedType(ListBox cmbTypes, List<Catalog> entityTypes) {
		int selectedTypeIndex = cmbTypes.getSelectedIndex();
		final Catalog type = entityTypes.get(selectedTypeIndex);
		
		return type;
	}

	private void createMasterData(final ClDoc clDoc,
			final ClickableTable<Entity> entityTable,
			final Catalog type, Entity selectedEntity) {

		final Entity entity;
		if (selectedEntity == null) {
			entity = createNewEntity(type);
	
			SRV.configurationService.listLayoutDefinitions(clDoc.getSession(), LayoutDefinition.FORM_LAYOUT, type.id, true, new DefaultCallback<List<LayoutDefinition>>(clDoc, "list masterdata layouts") {
	
				@Override
				public void onSuccess(List<LayoutDefinition> result) {
					if (result != null && !result.isEmpty()) {
						if (result.size() == 1) {
							LayoutDefinition ld = result.get(0);
							final Act act = new Act(ld.actClass);
							IView<Act> ar = ActRenderer.getActRenderer(clDoc, ld.xmlLayout, act, null);
							OnClick<PopupPanel> onClickSave = new OnClick<PopupPanel>() {

								@Override
								public void onClick(final PopupPanel popup) {
									entity.setName(act.getString("name"));
									SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "save new entity") {

										@Override
										public void onSuccess(Entity e) {
											act.setParticipant(e, Participation.PROTAGONIST);
											SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "saveAct") {

												@Override
												public void onSuccess(Act result) {
													popup.hide();
													entityTable.refresh();
												}
											});
										}
									});
								}
							};
							PopupManager.showModal("neu(e) " + type.shortText, ar, onClickSave, null); 
						} else {
							TabLayoutPanel tlp = new TabLayoutPanel(2.5, Unit.EM);
							final Collection<Act> acts = new ArrayList<Act>();
							for (LayoutDefinition ld:result) {
								Act act = new Act(ld.actClass);
								acts.add(act);
								IView<Act> ar = ActRenderer.getActRenderer(clDoc, ld.xmlLayout, act, null);
								tlp.add(ar, ld.actClass.name);
							}
							PopupManager.showModal("neu(e) " + type.shortText, tlp, new OnClick<PopupPanel>(){

								@Override
								public void onClick(final PopupPanel pp) {
									SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "save new entity") {

										@Override
										public void onSuccess(Entity e) {
											for (Act act:acts) {
												String name = act.getString("name");
												if (name != null) {
													entity.setName(name);
												}
												act.setParticipant(e, Participation.PROTAGONIST);
											}
											SRV.actService.save(clDoc.getSession(), acts, new DefaultCallback<Collection<Act>>(clDoc, "save masterdata" ) {

												@Override
												public void onSuccess(Collection<Act> result) {
													pp.hide();
													entityTable.refresh();
												}
											});
										}
									});
									
								}}, null); 
						}
					} else {
						new MessageBox("Fehlende Konfiguration", "Kein Layout fuer '" + type.shortText + "' definiert.", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_INFO).show();
					}
				}
			});
		} else {
			entity = selectedEntity;
			SRV.actService.findByEntity(clDoc.getSession(), entity, Participation.PROTAGONIST.id, new DefaultCallback<List<Act>>(clDoc, "load acts") {

				@Override
				public void onSuccess(List<Act> result) {
//					editMasterDataActs(result);
				}
			});
		}
		
		
//		final Act model = new Act(type.code);
//		final Entity entity;
//		
//		if (selectedEntity == null) {
//			entity = createNewEntity(type);
//		} else {
//			entity = selectedEntity;
//		}
//	
//		model.setParticipant(entity, Participation.MASTERDATA, null, null);
//		editEntityMasterData(clDoc, entityTable, model, entity);
		
	}
	
	private void editMasterDataActs(ClDoc clDoc, List<Act> result) {
//		SRV.configurationService.l
		
//		if (result.size() == 1) {
//			final Act act = new Act(ld.actClass);
//			IView<Act> ar = ActRenderer.getActRenderer(clDoc, ld.xmlLayout, act, null);
//			OnClick<PopupPanel> onClickSave = new OnClick<PopupPanel>() {
//
//				@Override
//				public void onClick(final PopupPanel popup) {
//					entity.setName(act.getString("name"));
//					SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "save new entity") {
//
//						@Override
//						public void onSuccess(Entity e) {
//							act.setParticipant(e, Participation.PROTAGONIST);
//							SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "saveAct") {
//
//								@Override
//								public void onSuccess(Act result) {
//									popup.hide();
//									entityTable.refresh();
//								}
//							});
//						}
//					});
//				}
//			};
//			PopupManager.showModal("neu(e) " + type.shortText, ar, onClickSave, null); 
//		} else {
//			TabLayoutPanel tlp = new TabLayoutPanel(2.5, Unit.EM);
//			final Collection<Act> acts = new ArrayList<Act>();
//			for (LayoutDefinition ld:result) {
//				Act act = new Act(ld.actClass);
//				acts.add(act);
//				IView<Act> ar = ActRenderer.getActRenderer(clDoc, ld.xmlLayout, act, null);
//				tlp.add(ar, ld.actClass.name);
//			}
//			PopupManager.showModal("neu(e) " + type.shortText, tlp, new OnClick<PopupPanel>(){
//
//				@Override
//				public void onClick(final PopupPanel pp) {
//					SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "save new entity") {
//
//						@Override
//						public void onSuccess(Entity e) {
//							for (Act act:acts) {
//								String name = act.getString("name");
//								if (name != null) {
//									entity.setName(name);
//								}
//								act.setParticipant(e, Participation.PROTAGONIST);
//							}
//							SRV.actService.save(clDoc.getSession(), acts, new DefaultCallback<Collection<Act>>(clDoc, "save masterdata" ) {
//
//								@Override
//								public void onSuccess(Collection<Act> result) {
//									pp.hide();
//									entityTable.refresh();
//								}
//							});
//						}
//					});
//					
//				}}, null); 
//		}
	}

	private Entity createNewEntity(Catalog type) {
		Entity entity;
		int t = type.id.intValue();
		switch (t) {
		case Entity.ENTITY_TYPE_PERSON:
			entity = new Person();
			break;
		case Entity.ENTITY_TYPE_ORGANISATION:
			entity = new Organisation();
			break;
		default:
			entity = new Entity();
			entity.type = t;
		}
		
		return entity;
	}

	private void editEntityMasterData(
			final ClDoc clDoc,
			final ClickableTable<Entity> entityTable,
			final Act model,
			final Entity entity
			) {
		int et = new Long(entity.type).intValue();
		switch (et) {
		case Entity.ENTITY_TYPE_PERSON:
			PersonEditor.editPerson(clDoc, (Person) entity);
			break;
		case Entity.ENTITY_TYPE_ORGANISATION:
		default:
			editGenericMasterData(clDoc, entityTable, model, entity);
		}
	}	
	
	private void editGenericMasterData(
			final ClDoc clDoc,
			final ClickableTable<Entity> entityTable,
			final Act model,
			final Entity entity
			) {
/*		
		SRV.configurationService.getLayoutDefinition(clDoc.getSession(), model.actClass.name, LayoutDefinition.MASTER_DATA_LAYOUT, new DefaultCallback<LayoutDefinition>(clDoc, "loadLayout") {

			@Override
			public void onSuccess(LayoutDefinition result) {
				final Form<Act> form = new Form<Act>(clDoc, model, null) {

					@Override
					protected void setup() {
					}
				};
				
				if (result != null) {
					final TextBox txtName = new TextBox();
					form.addLine("Anzeigename", txtName);
					txtName.setText(entity.getName());
					form.parseAndCreate(result.xmlLayout, false);
					form.showModal("Neu", 
						new OnClick<Act>() {

							@Override
							public void onClick(Act pp) {
								form.fromDialog();
								entity.setName(txtName.getText());
								SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "saveEntity") {

									@Override
									public void onSuccess(Entity result) {
										entity.id = result.id;
										SRV.actService.save(clDoc.getSession(), model, new DefaultCallback<Act>(clDoc, "") {

											@Override
											public void onSuccess(Act result) {
												if (entityTable != null) {
													entityTable.refresh();
												}
											}
										});
									}
								});
							}
						}, 
						null, 
						new OnClick<Act>() {

						@Override
						public void onClick(Act pp) {
//							form.close();
						}
					});
					form.toDialog();
				} else {
					new MessageBox("Fehlende Konfiguration", "Kein Layout fuer '" + model.actClass.name + "' definiert.", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_INFO).show();
				}
			}
		});
*/	}
	
	
	
}

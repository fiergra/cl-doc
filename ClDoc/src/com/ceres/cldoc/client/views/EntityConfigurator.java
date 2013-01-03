package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ERTree;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.PagesView;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.core.client.GWT;
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

	private ClickableTable<Entity> entityTable;

	private void setup(final ClDoc clDoc) {
		DockLayoutPanel listPanel = new DockLayoutPanel(Unit.EM);
		DockLayoutPanel treePanel = new DockLayoutPanel(Unit.EM);
		final ListBox cmbTypes = new ListBox();
		final ArrayList<Catalog> entityTypes = new ArrayList<Catalog>();
		
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
							public void onSuccess(List<Act> masterData) {
								Catalog type = getSelectedType(cmbTypes, entityTypes);
								editOrCreateMasterData(clDoc, type, entry, masterData);
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
				editOrCreateMasterData(clDoc, getSelectedType(cmbTypes, entityTypes), null, new ArrayList<Act>());
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

	
    private void createMasterDataEditor(final ClDoc clDoc, final Entity entity, final Collection<Act> masterData, List<LayoutDefinition> result, final Catalog entityType, final Runnable onSave) {
		if (result != null && !result.isEmpty()) {

//			if (result.size() == 1) {
//				LayoutDefinition ld = result.get(0);
//				final Act act = new Act(ld.actClass);
//				final IView<Act> ar = ActRenderer.getActRenderer(clDoc, ld.xmlLayout, act, null);
//				OnClick<PopupPanel> onClickSave = new OnClick<PopupPanel>() {
//
//					@Override
//					public void onClick(final PopupPanel popup) {
//						SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "save new entity") {
//
//							@Override
//							public void onSuccess(Entity e) {
//								act.setParticipant(e, Participation.PROTAGONIST);
//								SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "saveAct") {
//
//									@Override
//									public void onSuccess(Act result) {
//										popup.hide();
//										onSave.run();
//									}
//								});
//							}
//						});
//					}
//				};
//				PopupManager.showModal("neu(e) " + entityType.shortText, ar, onClickSave, null); 
//			} else {
				
				final PagesView<Act> tlp = new PagesView<Act>(null);
				final Collection<Act> acts = new ArrayList<Act>();
				for (LayoutDefinition ld:result) {
					Act act = getMasterDataAct(masterData, ld.actClass);
					acts.add(act);
					IView<Act> ar = ActRenderer.getActRenderer(clDoc, ld.xmlLayout, act, new Runnable() {
						
						@Override
						public void run() {
							// something was modified...
						}
					});
					tlp.addPage(ar, ld.actClass.name);
				}
				
				PopupManager.showModal("neu(e) " + entityType.shortText, tlp, new OnClick<PopupPanel>(){

					@Override
					public void onClick(final PopupPanel pp) {
						if (!setName(entity, masterData)) {
							entity.setName("NO NAME");
							GWT.log("missing field '" + Entity.DISPLAY_NAME + "' in masterdata forms.");
						}
						SRV.entityService.save(clDoc.getSession(), entity, new DefaultCallback<Entity>(clDoc, "save new entity") {

							@Override
							public void onSuccess(Entity e) {
								tlp.fromDialog();
								for (Act act:acts) {
									act.setParticipant(e, Participation.PROTAGONIST);
								}
								SRV.actService.save(clDoc.getSession(), acts, new DefaultCallback<Collection<Act>>(clDoc, "save masterdata" ) {

									@Override
									public void onSuccess(Collection<Act> result) {
										pp.hide();
										onSave.run();
									}
								});
							}
						});
						
					}}, null); 
				tlp.toDialog();
//			}
		} else {
			new MessageBox("Fehlende Konfiguration", "Kein Layout fuer '" + entityType.shortText + "' definiert.", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_INFO).show();
		}
    	
    }
	
    protected boolean setName(Entity entity, Collection<Act> masterData) {
    	String name = null;
    	boolean result = false;
    	
    	for (Act a:masterData) {
    		if (a.getString(Entity.DISPLAY_NAME) != null) {
    			if (name != null) {
    				GWT.log("name already set: " + name + " - " + a.getString(Entity.DISPLAY_NAME));
    			} else {
    				entity.setName(a.getString(Entity.DISPLAY_NAME));
    				result = true;
    			}
    		}
    	}
    	return result;
	}

	private Act getMasterDataAct(Collection<Act> masterData, ActClass actClass) {
    	Act masterDataAct = null; 
    	Iterator<Act> iter = masterData != null ? masterData.iterator() : null;
    	
    	while (iter != null && iter.hasNext() && masterDataAct == null) {
    		Act next = iter.next();
    		if (next.actClass.equals(actClass)) {
    			masterDataAct = next;
    		}
    	}
    	
    	if (masterDataAct == null) {
    		masterDataAct = new Act(actClass);
    		masterData.add(masterDataAct);
    	}
    	
		return masterDataAct;
	}

	private void editOrCreateMasterData(final ClDoc clDoc,
			final Catalog type, final Entity selectedEntity, final List<Act> masterData) {

			SRV.configurationService.listLayoutDefinitions(clDoc.getSession(), LayoutDefinition.FORM_LAYOUT, type.id, true, new DefaultCallback<List<LayoutDefinition>>(clDoc, "list masterdata layouts") {
	
				@Override
				public void onSuccess(final List<LayoutDefinition> layoutDefinitions) {
					final Runnable refresh = new Runnable() {
						
						@Override
						public void run() {
							entityTable.refresh();
						}
					};

					createMasterDataEditor(clDoc, selectedEntity == null ? createNewEntity(type) : selectedEntity, masterData, layoutDefinitions, type, refresh);
				}
			});
		
		
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
	
	private Entity createNewEntity(Catalog type) {
		Entity entity;
		int t = type.id.intValue();
		switch (t) {
		case Entity.ENTITY_TYPE_PERSON:
			entity = new Person();
			break;
//		case Entity.ENTITY_TYPE_ORGANISATION:
//			entity = new Organisation();
//			break;
		default:
			entity = new Entity();
			entity.type = t;
		}
		
		return entity;
	}
	
	
	
}

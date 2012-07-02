package com.ceres.cldoc.client.controls;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.CatalogListBox;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.MessageBox;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.client.views.PopupManager;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ERTree extends ClickableTree<EntityRelation> {
	private Entity entity;
	private boolean asSubject = false;
	private final OnClick<EntityRelation> onDoubleClick;
	
	public ERTree(final ClDoc clDoc,
			OnClick<EntityRelation> onClick, 
			OnClick<EntityRelation> onDoubleClick, 
			boolean showRefresh) {
		super(clDoc, true);
		setListRetrieval(new ListRetrievalService<EntityRelation>() {
			
			@Override
			public void retrieve(String filter,	AsyncCallback<List<EntityRelation>> callback) {
				SRV.entityService.listRelations(clDoc.getSession(), entity, asSubject, callback);
			}
		});
		setOnClick(onClick);
		this.onDoubleClick = onDoubleClick;
	}

	@Override
	protected Widget itemRenderer(final EntityRelation er) {
		Grid grid = new Grid(1, 6);
		grid.setWidget(0, 0, new Label(String.valueOf(er.id)));
		grid.setWidget(0, 1, new HTML("<b>" + er.type.shortText + "</b>"));
		grid.setWidget(0, 2, getDirectionImage(asSubject));
		Entity e = asSubject ? er.object : er.subject;
		grid.setWidget(0, 3, getEntityWidget(e)); //new HTML("<i>" + (asSubject ? er.object.name : er.subject.name) + "</i>"));
		
		grid.setWidget(0, 4, getAddRelationButton(e));
		grid.setWidget(0, 5, getDeleteRelationButton(er));
		
		return grid;
	}


	private Widget getAddRelationButton(final Entity entity) {
		Image pbAdd = new Image("icons/16/File-New-icon.png");
		pbAdd.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addRelation(entity);
			}
		});
		return pbAdd;
	}
	
	private Widget getDeleteRelationButton(final EntityRelation er) {
		Image pbDelete = new Image("icons/16/File-Delete-icon.png");
		pbDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new MessageBox("Loeschen", "Wollen Sie die Relation entfernen?" , MessageBox.MB_YES | MessageBox.MB_YES, MESSAGE_ICONS.MB_ICON_QUESTION){

					@Override
					protected void onClick(int result) {
						if (result == MessageBox.MB_YES) {
							deleteRelation(er);
						}
					}
					
				}.show();
				
			}
		});
		return pbDelete;
	}

	private Widget getDirectionImage(boolean asSubject) {
		Image img = asSubject ? new Image("icons/16/arrow-mini-right-icon.png") : new Image("icons/16/arrow-mini-left-icon.png");
		img.setPixelSize(16, 16);
		return img;
	}

	@Override
	protected TreeItem getRoot() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(getEntityWidget(entity));
		hp.add(getAddRelationButton(entity));
		return new TreeItem(hp);
	}

	private Widget getEntityWidget(final Entity entity) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(2);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image w = null;
		switch (entity.type) {
		case Entity.ENTITY_TYPE_PERSON:
			w = new Image("icons/16/man-icon.png");
			break;
		case Entity.ENTITY_TYPE_ORGANISATION:
			w = new Image("icons/16/chart-organisation-icon.png");
			break;
		case 1008:
			w = new Image("icons/16/home-icon.png");
			break;
		case 1007:
			w = new Image("icons/16/City-icon.png");
			break;
		default:
			w = new Image("icons/16/Button-Help-icon.png");
			break;
		}

		w.setTitle(String.valueOf(entity.type));
		hp.add(w);
		hp.add(new Label(entity.getName()));

		return hp;
	}

	private PopupPanel popup;
	
	protected void deleteRelation(EntityRelation er) {
		SRV.entityService.delete(clDoc.getSession(), entity, new DefaultCallback<Void>(clDoc, "delete ER") {

			@Override
			public void onSuccess(Void result) {
				refresh();
			}
		});
		
	}
	
	protected void addRelation(Entity entity) {
		int row = 0;
		Grid content = new Grid(4, 3);
		final EntityRelation er = new EntityRelation();
		final CatalogListBox cmbTypes = new CatalogListBox(clDoc, "MASTERDATA.EntityTypes");
		final CatalogListBox cmbRelationTypes = new CatalogListBox(clDoc, "MASTERDATA.ER");
		
		OnDemandComboBox<Entity> cmbEntities = new OnDemandComboBox<Entity>(clDoc, 
				new ListRetrievalService<Entity>() {

					@Override
					public void retrieve(String filter, AsyncCallback<List<Entity>> callback) {
						Long typeId = cmbTypes.getSelected() != null ? cmbTypes.getSelected().id : null;
						
						if (typeId != null) {
							SRV.entityService.search(clDoc.getSession(), filter, typeId.intValue(), callback);
						} else {
							callback.onSuccess(null);
						}
					}
				}, 
				new LabelFunction<Entity>() {

					@Override
					public String getLabel(Entity entity) {
						return entity.getName();
					}

					@Override
					public String getValue(Entity entity) {
						return String.valueOf(entity.id);
					}
				}, 
				new OnDemandChangeListener<Entity>() {

					@Override
					public void onChange(Entity oldValue, Entity newValue) {
						if (!asSubject) {
							er.subject = newValue;
						} else {
							er.object = newValue;
						}
					}
				});
		
		if (asSubject) {
			er.subject = entity;
			content.setWidget(row, 0, newLabel("Subject"));
			content.setWidget(row, 1, new Label(entity.getName()));
			row++;
		} else {
			er.object = entity;
			content.setWidget(row, 0, newLabel("Typ"));
			content.setWidget(row, 1, cmbTypes);
			row++;
			content.setWidget(row, 0, newLabel("Subject"));
			content.setWidget(row, 1, cmbEntities);
			row++;
		}
		content.setWidget(row, 0, newLabel("Relation"));
		content.setWidget(row, 1, cmbRelationTypes);
		row++;

		if (asSubject) {
			content.setWidget(row, 0, newLabel("Typ"));
			content.setWidget(row, 1, cmbTypes);
			row++;
			content.setWidget(row, 0, newLabel("Object"));
			content.setWidget(row, 1, cmbEntities);
			row++;
			
		} else {
			content.setWidget(row, 0, newLabel("Object"));
			content.setWidget(row, 1, new Label(entity.getName()));
		}

		popup = PopupManager.showModal(er, "Neue Beziehung", content, 
				new OnClick<EntityRelation>() {

					@Override
					public void onClick(EntityRelation er) {
						if (cmbRelationTypes.getSelected() != null) {
							er.type = cmbRelationTypes.getSelected();
							SRV.entityService.save(clDoc.getSession(), er, new DefaultCallback<EntityRelation>(clDoc, "saveER") {
	
								@Override
								public void onSuccess(EntityRelation result) {
									popup.hide();
									refresh();
								}
							});
						}
					}
				}, 
				null, 
				new OnClick<EntityRelation>() {

					@Override
					public void onClick(EntityRelation er) {
						popup.hide();
					}
				});
		
	}

	private Widget newLabel(String string) {
		Label label = new Label(string);
		label.addStyleName("formLabel");
		return label;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
		refresh();
	}

	public void setDirection(Boolean asSubject) {
		this.asSubject = asSubject;
		refresh();
	}
}

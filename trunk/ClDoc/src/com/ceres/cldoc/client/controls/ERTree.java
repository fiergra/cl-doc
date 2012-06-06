package com.ceres.cldoc.client.controls;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ERTree extends ClickableTree<EntityRelation> {
	
	private Entity entity;
	private boolean asSubject = false;
	private OnClick<EntityRelation> onDoubleClick;
	
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
		Grid grid = new Grid(1, 4);
		grid.setWidget(0, 0, new Label(String.valueOf(er.id)));
		grid.setWidget(0, 1, new HTML("<b>" + er.type.shortText + "</b>"));
		grid.setWidget(0, 2, getDirectionImage(asSubject));
		grid.setWidget(0, 3, getEntityWidget(asSubject ? er.object : er.subject)); //new HTML("<i>" + (asSubject ? er.object.name : er.subject.name) + "</i>"));
		
		return grid;
//		HTML html = new HTML(er.id + "|<b>" + er.type.shortText + "</b> - <i>" + (asSubject ? er.object.name : er.subject.name) + "</i>");
//		html.addDoubleClickHandler(new DoubleClickHandler() {
//			
//			@Override
//			public void onDoubleClick(DoubleClickEvent event) {
//				onDoubleClick.onClick(er);
//			}
//		});
//		
//		return html;
	}


	private Widget getDirectionImage(boolean asSubject) {
		Image img = asSubject ? new Image("icons/16/arrow-mini-right-icon.png") : new Image("icons/16/arrow-mini-left-icon.png");
		img.setPixelSize(16, 16);
		return img;
	}

	@Override
	protected TreeItem getRoot() {
		return new TreeItem(getEntityWidget(entity));
	}

	private Widget getEntityWidget(Entity entity) {
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
		default:
			w = new Image("icons/16/Button-Help-icon.png");
			break;
		}

		w.setTitle(String.valueOf(entity.type));
		hp.add(w);
		hp.add(new Label(entity.name));
		return hp;
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

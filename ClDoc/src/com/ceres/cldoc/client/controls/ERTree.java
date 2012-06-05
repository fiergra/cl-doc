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
import com.google.gwt.user.client.ui.HTML;
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
		HTML html = new HTML(er.id + "|<b>" + er.type.shortText + "</b> - <i>" + (asSubject ? er.object.name : er.subject.name) + "</i>");
		html.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				onDoubleClick.onClick(er);
			}
		});
		
		return html;
	}


	@Override
	protected TreeItem getRoot() {
		return new TreeItem(entity.name);
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

package com.ceres.cldoc.client.controls;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class XERTree extends Tree {
	private long typeId;
	private Entity entity;
	private boolean asSubject = false;
	private final ClDoc clDoc;
	
	public XERTree(ClDoc clDoc) {
		super();
		this.clDoc = clDoc;
		addStyleName("resultTable");
	}
	
	public void setEntityType(long typeId) {
		this.typeId = typeId;
		refresh();
	}

	public void refresh() {
		clear();
		SRV.entityService.listRelations(clDoc.getSession(), entity, asSubject, new DefaultCallback<List<EntityRelation>>(clDoc, "listRelations") {

			@Override
			public void onSuccess(List<EntityRelation> result) {
				TreeItem root = new TreeItem(entity.name);
				addTreeItems(root, result);
				addItem(root);
				root.setState(true);
				setSelectedItem(root);
			}
			
			private void addTreeItems(TreeItem parent, List<EntityRelation> relations) {
				for (EntityRelation er : relations) {
					TreeItem ti = relation2TreeItem(er);
					parent.addItem(ti);
					if (er.hasChildren()) {
						addTreeItems(ti, er.children);
					}
				}
			}
		
		});
	}

	private TreeItem relation2TreeItem(EntityRelation er) {
		HTML html = new HTML(er.id + "|<b>" + er.type.shortText + "</b> - <i>" + (asSubject ? er.object.name : er.subject.name) + "</i>");
		TreeItem ti = new TreeItem(html);

		DropController dc = new SimpleDropController(html) {
			
		};
		clDoc.getDragController().registerDropController(dc);
		
		ti.setUserObject(er);
		return ti;
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

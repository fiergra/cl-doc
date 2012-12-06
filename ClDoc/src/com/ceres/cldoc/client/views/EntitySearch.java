package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTree;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


public class EntitySearch extends SplitLayoutPanel {

	private Entity entity;

	public EntitySearch(final ClDoc clDoc, long entityId) {
		final ClickableTree<EntityRelation> tree = new ClickableTree<EntityRelation>(clDoc, true) {

			private Image getTypeImage(Entity entity) {
				Image w = null;
				int et = new Long(entity.type).intValue();
				switch (et) {
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
				}
				return w;
			}
	
			
			@Override
			protected Widget itemRenderer(EntityRelation er) {
				Image img = getTypeImage(er.subject);
				if (img != null) {
					HorizontalPanel hp = new HorizontalPanel();
					hp.add(img);
					hp.add(new Label(er.subject.getName()));
					return hp;
				} else {
					return new Label(er.subject.getName());
				}
			}

			@Override
			protected TreeItem getRoot() {
				return new TreeItem(entity.getName());
			}
			
			
		};
//		final HistoryView hv = new HistoryView(clDoc, null);
		SRV.entityService.findById(clDoc.getSession(), entityId, new DefaultCallback<Entity>(clDoc, "loadEntity") {

			@Override
			public void onSuccess(final Entity entity) {
				EntitySearch.this.entity = entity;
				tree.setListRetrieval(new ListRetrievalService<EntityRelation>() {
					
					@Override
					public void retrieve(String filter, AsyncCallback<List<EntityRelation>> callback) {
						SRV.entityService.listRelations(clDoc.getSession(), entity, false, callback);
					}
				});
				
				tree.setOnClick(new OnClick<EntityRelation>() {
					
					@Override
					public void onClick(EntityRelation pp) {
						if (pp != null) {
							HTML header = new HTML(pp.subject.getName());
							header.addStyleName("nameAndId");
							EntityFile<Entity> ef = new EntityFile<Entity>(clDoc, pp.subject, header, "CLDOC.PERSONALFILE");
							if (getCenter() != null) {
								remove(getCenter());
							}
							add(ef);
						}
					}
				});
				tree.refresh();
			}
		});
		addWest(tree, 300);
//		add(hv);
	}

}

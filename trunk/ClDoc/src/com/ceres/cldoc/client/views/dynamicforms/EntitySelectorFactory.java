package com.ceres.cldoc.client.views.dynamicforms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.EntityListBox;
import com.ceres.cldoc.model.Entity;
import com.ceres.core.IApplication;
import com.ceres.core.IEntity;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorWidgetLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class EntitySelectorFactory implements ILinkFactory {
	private final IApplication application;
	
	public EntitySelectorFactory(final IApplication application) {
		this.application = application;
	}
	@Override
	public InteractorWidgetLink createLink(final Interactor interactor, final String fieldName,
			HashMap<String, String> attributes) {
		
		final EntityListBox<Entity>hbl = new EntityListBox(application, getTypeId(attributes));
		final InteractorWidgetLink link = new InteractorWidgetLink(interactor, fieldName, hbl, attributes) {
			
			private boolean isSelecting = false;
			
			@Override
			public void toDialog(Map<String, Serializable> item) {
				hbl.setSelected((Entity) get(item, fieldName));
			}
			
			@Override
			public void fromDialog(Map<String, Serializable> item) {
				item.put(fieldName, hbl.getSelected());
			}

			@Override
			public boolean isEmpty() {
				return hbl.getSelected() == null && !isSelecting;
			}
		};
		
		hbl.addSelectionChangedHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				interactor.onChange(link);
			}
		});

		return link;
	}
	private int getTypeId(HashMap<String, String> attributes) {
		return Integer.valueOf(attributes.get("entityType"));
	}

}

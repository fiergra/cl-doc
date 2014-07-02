package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Entity;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class EntityListBox <T extends Entity> extends ListBox implements IEntitySelector<T>{
	
	private T selected;
	private List<T> entities;

	public EntityListBox(final ClDoc clDoc, final int typeId, final boolean hasNullValue) {
		SRV.entityService.list(clDoc.getSession(), typeId, new DefaultCallback<List<T>>(clDoc, "retrieve entities") {

			@Override
			public void onResult(List<T> result) {
				entities = result;
				
				if (hasNullValue) {
					entities.add(0, null);
				}
				
				for (T e:result) {
					addItem(getLabel(e));
				}
				
				setSelected(selected);
			}
		});
		addStyleName("humanBeingListBox");
		addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				int index = getSelectedIndex();
				selected = index != -1 ? entities.get(index) : null;
			}
		});
	}


	@Override
	public T getSelected() {
		return selected;
	}

	protected String getLabel(T e) {
		return e != null ? e.getName() : "---";
	}

	@Override
	public boolean setSelected(T entity) {
		boolean result = false;
		
		selected = entity;
		if (entities != null) {
			int index = entities.indexOf(entity);
			setSelectedIndex(index);
			result = true;
		}
		return result;
	}


	@Override
	public void addSelectionChangedHandler(ChangeHandler changeHandler) {
		addChangeHandler(changeHandler);
	}

	
}

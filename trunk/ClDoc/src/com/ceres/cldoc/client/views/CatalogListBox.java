package com.ceres.cldoc.client.views;

import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.Catalog;
import com.google.gwt.user.client.ui.ListBox;
import com.googlecode.objectify.Key;

public class CatalogListBox extends ListBox implements IEntitySelector <Catalog> {
	
	private List<Catalog> catalogs;
	private Catalog selected;
	private boolean isMandatory = false;
	private Catalog emptyRecord;

	public CatalogListBox(String parentCode) {
		addStyleName("cataloglistbox");
		SRV.configurationService.listCatalogs(parentCode, new DefaultCallback<List<Catalog>>() {

			@Override
			public void onSuccess(List<Catalog> result) {
				CatalogListBox.this.catalogs = result;
				
				if (!isMandatory) {
					emptyRecord = new Catalog();
					emptyRecord.code = "";
					emptyRecord.shortText = "---";
					emptyRecord.text = "---";
					result.add(0, emptyRecord);
				}

				
				addListItems();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
	}


	private void addListItems() {
		
		for (Catalog c: catalogs) {
			addItem(c.shortText, c.code);
		}
	}

	@Override
	public boolean setSelected(Catalog catalog) {
		boolean found = false;
		
		if (catalogs != null) {
			Iterator<Catalog> i = catalogs.iterator();
			int index = 0;
			
			while (!found && i.hasNext()) {
				if (i.next().code.equals(catalog.code)) {
					found = true;
				} else {
					index++;
				}
			}
			
			if (found) {
				setSelectedIndex(index);
			}
		} else {
			selected = catalog;
		}
		return found;
	}

	@Override
	public Catalog getSelected() {
		int index = getSelectedIndex();
		Catalog selected = index >= 0 ? catalogs.get(index) : null; 
		
		return selected != null && !selected.equals(emptyRecord) ? selected : null;
	}
}

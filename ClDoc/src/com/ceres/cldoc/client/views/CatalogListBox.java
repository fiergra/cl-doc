package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.user.client.ui.ListBox;

public class CatalogListBox extends ListBox implements IEntitySelector <Catalog> {
	
	private List<Catalog> catalogs;
	private Catalog selected;
	private boolean isMandatory = false;
	private Catalog emptyRecord;

	public CatalogListBox(ClDoc clDoc, String parentCode) {
		addStyleName("cataloglistbox");
		SRV.catalogService.listCatalogs(clDoc.getSession(), parentCode, new DefaultCallback<Collection<Catalog>>(clDoc, "listCatalogs") {

			@Override
			public void onSuccess(Collection<Catalog> result) {
				CatalogListBox.this.catalogs = new ArrayList<Catalog>(result);
				
				if (!isMandatory) {
					emptyRecord = new Catalog();
					emptyRecord.code = "";
					emptyRecord.shortText = "---";
					emptyRecord.text = "---";
					CatalogListBox.this.catalogs.add(0, emptyRecord);
				}

				
				addListActs();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
	}


	private void addListActs() {
		
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

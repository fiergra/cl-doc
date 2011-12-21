package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.Catalog;
import com.google.gwt.user.client.ui.TreeItem;

public class CatalogTreeItem extends TreeItem {
	private Catalog catalog;
	
	public CatalogTreeItem(Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public String getText() {
		return catalog.shortText;
	}
	
	
}

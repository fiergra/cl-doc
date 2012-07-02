package com.ceres.cldoc.client.controls;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class CatalogSelectionTree extends ClickableTree<Catalog> {

	public CatalogSelectionTree(final ClDoc clDoc, OnClick onClick, final String parentCode) {
		super(clDoc, null, onClick, true);
		setListRetrieval(new ListRetrievalService<Catalog>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Catalog>> callback) {
				SRV.configurationService.listCatalogs(clDoc.getSession(), parentCode, callback);
			}
		});
		refresh();
	}

	@Override
	protected Widget itemRenderer(Catalog node) {
		return super.itemRenderer(node);
	}

	
}

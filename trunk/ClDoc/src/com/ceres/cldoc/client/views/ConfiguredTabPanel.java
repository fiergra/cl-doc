package com.ceres.cldoc.client.views;

import java.util.Collection;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.DynamicLoader;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ConfiguredTabPanel<T> extends TabLayoutPanel {

	public ConfiguredTabPanel(ClDoc clDoc, String name, T model) {
		super(3, Unit.EM);
		setup(clDoc, name, model);
	}

	private void setup(final ClDoc clDoc, String parent, final T model) {
		SRV.catalogService.listCatalogs(clDoc.getSession(), parent, new DefaultCallback <Collection<Catalog>>(clDoc, "listCatalogs") {
			
			@Override
			public void onSuccess(Collection<Catalog> result) {
				for (Catalog catalog : result) {
					add(DynamicLoader.create(clDoc, catalog.code, model), catalog.text, false);
				}
				selectTab(0);
			}
			
		});
	}
	
}

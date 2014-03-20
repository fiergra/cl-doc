package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.Action;
import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.DynamicLoader;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfiguredTabPanel<T> extends TabLayoutPanel {

	public ConfiguredTabPanel(ClDoc clDoc, String name, T model) {
		super(2.5, Unit.EM);
		setup(clDoc, name, model);
	}

	private void setup(final ClDoc clDoc, String parent, final T model) {
		SRV.catalogService.listCatalogs(clDoc.getSession(), parent, new DefaultCallback <List<Catalog>>(clDoc, "listCatalogs") {
			
			@Override
			public void onResult(List<Catalog> result) {
				if (!result.isEmpty()) {
					for (Catalog catalog : result) {
						if (clDoc.getSession().isAllowed(new Action(catalog.code, Catalog.VIEW.code))) {
							addTab(clDoc, catalog, model);
						}
					}
					if (getWidgetCount() > 0) {
						selectTab(0);
					}
					
				}
			}
			
		});
	}

	protected void addTab(ClDoc clDoc, Catalog catalog, T model) {
		Widget w = DynamicLoader.create(clDoc, catalog, model);
		add(w, new Label("> " + catalog.text));
	}
	
}

package com.ceres.cldoc.client.views;

import java.util.Collection;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.DynamicLoader;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ConfiguredTabPanel<T> extends TabLayoutPanel {

	public ConfiguredTabPanel(Session session, String name, T model) {
		super(3, Unit.EM);
		setup(session, name, model);
	}

	private void setup(final Session session, String parent, final T model) {
		SRV.configurationService.listCatalogs(session, parent, new DefaultCallback <Collection<Catalog>>() {
			
			@Override
			public void onSuccess(Collection<Catalog> result) {
				for (Catalog catalog : result) {
					add(DynamicLoader.create(session, catalog.code, model), catalog.text, false);
				}
				selectTab(0);
			}
			
		});
	}
	
}

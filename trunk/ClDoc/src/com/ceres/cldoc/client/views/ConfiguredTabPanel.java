package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.DynamicLoader;
import com.ceres.cldoc.client.service.SRV;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ConfiguredTabPanel<T> extends TabLayoutPanel {

	public ConfiguredTabPanel(String name, T model) {
		super(3, Unit.EM);
		setup(name, model);
	}

	private void setup(String name, final T model) {
		SRV.configurationService.listChildren(name, new DefaultCallback <List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				for (String name : result) {
					add(DynamicLoader.create(name, model), name, false);
				}
				selectTab(0);
			}
			
		});
	}
	
}

package com.ceres.cldoc.client.views.dynamicforms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.cldoc.client.views.CatalogMultiSelect;
import com.ceres.cldoc.client.views.IEntitySelector;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.core.IApplication;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class CatalogMultiSelectorFactory implements ILinkFactory {

	private final IApplication application;
	
	public CatalogMultiSelectorFactory(IApplication application) {
		this.application = application;
	}
	
	@Override
	public InteractorLink createLink(final Interactor interactor, final String fieldName,
			HashMap<String, String> attributes) {
		String sMax = attributes.get("columns");
		int maxCol = sMax != null ? Integer.valueOf(sMax) : 6;

		final IEntitySelector<CatalogList> hbl = 
				new CatalogMultiSelect(application, attributes.get("parent"), maxCol, attributes.get("orientation"));
		final InteractorLink link = new InteractorLink(interactor, fieldName, (Widget) hbl, attributes) {
			
			@Override
			public void toDialog(Map<String, Serializable> item) {
				Serializable catalogList = item.get(fieldName);
				if (catalogList != null) {
					hbl.setSelected((CatalogList) catalogList);
				}

			}
			
			@Override
			public void fromDialog(Map<String, Serializable> item) {
				item.put(fieldName, hbl.getSelected());
			}

			@Override
			public boolean isEmpty() {
				return hbl.getSelected() == null;
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

}

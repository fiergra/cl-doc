package com.ceres.cldoc.client.views.dynamicforms;

import java.io.Serializable;
import java.util.HashMap;

import com.ceres.cldoc.client.views.CatalogMultiSelect;
import com.ceres.cldoc.client.views.IEntitySelector;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.core.IApplication;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.INamedValues;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class CatalogMultiSelectorFactory implements ILinkFactory {

	@Override
	public InteractorLink createLink(final IApplication application, final Interactor interactor, String fieldName,
			HashMap<String, String> attributes) {
		String sMax = attributes.get("columns");
		int maxCol = sMax != null ? Integer.valueOf(sMax) : 6;

		final IEntitySelector<CatalogList> hbl = 
				new CatalogMultiSelect(application, attributes.get("parent"), maxCol, attributes.get("orientation"));
		final InteractorLink link = new InteractorLink(interactor, fieldName, (Widget) hbl, attributes) {
			
			@Override
			public void toDialog(INamedValues item) {
				Serializable catalogList = item.getValue(fieldName);
				if (catalogList != null) {
					hbl.setSelected((CatalogList) catalogList);
				}

			}
			
			@Override
			public void fromDialog(INamedValues item) {
				item.setValue(fieldName, hbl.getSelected());
			}

			@Override
			public boolean isEmpty() {
				return hbl.getSelected().isEmpty();
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

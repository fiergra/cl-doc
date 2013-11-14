package com.ceres.cldoc.client.views.dynamicforms;

import java.io.Serializable;
import java.util.HashMap;

import com.ceres.cldoc.client.views.CatalogListBox;
import com.ceres.cldoc.client.views.CatalogRadioGroup;
import com.ceres.cldoc.client.views.IEntitySelector;
import com.ceres.cldoc.model.Catalog;
import com.ceres.core.IApplication;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.INamedValues;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class CatalogSingleSelectorFactory implements ILinkFactory {

	final private boolean useList;
	
	public CatalogSingleSelectorFactory(boolean useList) {
		this.useList = useList;
	}
	@Override
	public InteractorLink createLink(final IApplication application, final Interactor interactor, String fieldName,
			HashMap<String, String> attributes) {
		final IEntitySelector<Catalog> hbl = useList ? 
				new CatalogListBox(application, attributes.get("parent")) :
					new CatalogRadioGroup(application, attributes.get("parent"), attributes.get("orientation"));
				
		final InteractorLink link = new InteractorLink(interactor, fieldName, (Widget) hbl, attributes) {
			
			@Override
			public void toDialog(INamedValues item) {
				Serializable catalog = item.getValue(fieldName);
				if (catalog != null) {
					hbl.setSelected((Catalog) catalog);
				}

			}
			
			@Override
			public void fromDialog(INamedValues item) {
				item.setValue(fieldName, hbl.getSelected());
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

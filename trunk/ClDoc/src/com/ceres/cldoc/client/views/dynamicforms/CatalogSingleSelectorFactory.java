package com.ceres.cldoc.client.views.dynamicforms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.cldoc.client.views.CatalogListBox;
import com.ceres.cldoc.client.views.CatalogRadioGroup;
import com.ceres.cldoc.client.views.IEntitySelector;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IApplication;
import com.ceres.dynamicforms.client.ILinkFactory;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.InteractorWidgetLink;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public class CatalogSingleSelectorFactory implements ILinkFactory {

	final private boolean useList;
	final private IApplication application;

	public CatalogSingleSelectorFactory(IApplication application, boolean useList) {
		this.useList = useList;
		this.application = application;
	}
	
	@Override
	public InteractorWidgetLink createLink(final Interactor interactor, final String fieldName,
			HashMap<String, String> attributes) {
		final IEntitySelector<Catalog> hbl = useList ? 
				new CatalogListBox(application, attributes.get("parent")) :
					new CatalogRadioGroup(application, attributes.get("parent"), attributes.get("orientation"));
				
		final InteractorWidgetLink link = new InteractorWidgetLink(interactor, fieldName, (Widget) hbl, attributes) {
			
			@Override
			public void toDialog(Map<String, Serializable> item) {
				Serializable catalog = item.get(fieldName);
				if (catalog != null) {
					hbl.setSelected((Catalog) catalog);
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

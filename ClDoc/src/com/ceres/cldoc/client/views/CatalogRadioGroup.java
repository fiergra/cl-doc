package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;

public class CatalogRadioGroup extends HorizontalPanel implements IEntitySelector <Catalog> {
	private static int groupCount = 0;
	protected HashMap<Long, RadioButton> buttons;
	private Catalog selected;

	public CatalogRadioGroup(ClDoc clDoc, String parentCode, String orientation) {
		super();
		setPixelSize(300, 25);
		SRV.catalogService.listCatalogs(clDoc.getSession(), parentCode, new DefaultCallback<Collection<Catalog>>(clDoc, "listCatalogs") {

			@Override
			public void onSuccess(Collection<Catalog> result) {
				buttons = new HashMap<Long, RadioButton>(result.size());
				String groupName = String.valueOf("rbgroup" + ++groupCount);
				
				for (final Catalog c:result) {
					RadioButton rb = new RadioButton(groupName, c.shortText);
					add(rb);
					buttons.put(c.id, rb);
					rb.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							onClickCatalog(c);
						}
					});
				}
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		
	}

	private void onClickCatalog(Catalog selected) {
		if (this.selected != selected) {
			this.selected = selected;
			for (ChangeHandler ch:changeHandlers) {
				ch.onChange(null);
			}
		}
	}
	
	private List<ChangeHandler> changeHandlers = new ArrayList<ChangeHandler>();
	
	public void addChangeHandler(ChangeHandler changeHandler) {
		changeHandlers.add(changeHandler);
	}

	@Override
	public boolean setSelected(Catalog catalog) {
		selected = catalog;
		if (buttons != null) {
			RadioButton rb = buttons.get(catalog.id);
			if (rb != null) {
				rb.setValue(true);
				return true;
			}
		}
		return false;
	}

	@Override
	public Catalog getSelected() {
		return selected;
	}

}

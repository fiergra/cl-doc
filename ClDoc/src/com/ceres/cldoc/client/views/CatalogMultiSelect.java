package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.CatalogList;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;

public class CatalogMultiSelect extends FlexTable implements
		IEntitySelector<CatalogList> {
	protected HashMap<Long, CheckBox> buttons;
	private CatalogList selected;

	public CatalogMultiSelect(ClDoc clDoc, String parentCode, final int maxCol,
			String orientation) {
		super();
		SRV.catalogService.listCatalogs(clDoc.getSession(), parentCode,
				new DefaultCallback<List<Catalog>>(clDoc, "listCatalogs") {

					@Override
					public void onSuccess(List<Catalog> result) {
						buttons = new HashMap<Long, CheckBox>(result.size());

						// if (result.size() >= maxCol) {
						int row = 0;
						int col = 0;

						for (final Catalog c : result) {
							setWidget(row, col++, createCheckBox(c));
							if (col % maxCol == 0) {
								row++;
								col = 0;
							}
						}

						if (selected != null) {
							setSelected(selected);
						}
					}
				});

	}

	private CheckBox createCheckBox(final Catalog c) {
		final CheckBox rb = new CheckBox(c.shortText);
		rb.setTitle(c.text);
		buttons.put(c.id, rb);
		rb.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onClickCatalog(c, rb.getValue());
			}
		});
		return rb;
	}

	private void onClickCatalog(Catalog selectedChild, boolean isSelected) {
		if (this.selected == null) {
			this.selected = new CatalogList();
		}

		if (isSelected) {
			this.selected.addValue(selectedChild);
		} else {
			this.selected.removeValue(selectedChild);
		}

		for (ChangeHandler ch : changeHandlers) {
			ch.onChange(null);
		}
	}

	private final List<ChangeHandler> changeHandlers = new ArrayList<ChangeHandler>();

	public void addChangeHandler(ChangeHandler changeHandler) {
		changeHandlers.add(changeHandler);
	}

	@Override
	public boolean setSelected(CatalogList catalogList) {
		boolean result = false;
		selected = catalogList;
		if (buttons != null) {
			if (selected != null && catalogList.list != null) {
				for (Catalog c : catalogList.list) {
					CheckBox rb = buttons.get(c.id);
					if (rb != null) {
						rb.setValue(true);
						result = true;
					}
				}
			} else {
				Iterator<CheckBox> biter = buttons.values().iterator();
				while (biter.hasNext()) {
					biter.next().setValue(false);
				}
			}
		}
		return result;
	}

	@Override
	public CatalogList getSelected() {
		return selected;
	}

}

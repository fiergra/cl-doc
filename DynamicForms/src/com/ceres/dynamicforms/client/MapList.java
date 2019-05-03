package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ceres.dynamicforms.client.components.MapListRenderer;
import com.google.gwt.user.client.ui.Widget;

public class MapList extends MapListRenderer {

	private List<String> colDefs = new ArrayList<>();
	
	public MapList(ITranslator translator, String[] labels, Interactor<Map<String, Serializable>> interactor) {
		super(translator, labels, interactor);
	}

	@Override
	protected Map<String, Serializable> newAct() {
		return new HashMap<>();
	}

	@Override
	protected boolean isValid(Interactor interactor) {
		return interactor.isValid();
	}

	@Override
	protected boolean canRemove(Map<String, Serializable> act) {
		return true;
	}

	@Override
	protected void createNewRow(int row, Interactor interactor) {
		for (int col = 0; col < colDefs.size(); col++) {
			String colDef = colDefs.get(col);
			System.out.println("cr: " + row + ": "+ colDef);
			Widget w = WidgetCreator.createWidget(colDef, interactor);
			setWidget(row, col, w);
		}
	}

	public void addColDef(String colDef) {
		colDefs.add(colDef);
	}

}

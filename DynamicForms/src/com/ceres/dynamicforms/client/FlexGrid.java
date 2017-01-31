package com.ceres.dynamicforms.client;

import java.util.HashMap;
import java.util.Iterator;

import com.ceres.dynamicforms.client.components.EnabledHorizontalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class FlexGrid extends FlexTable {
	
	public static class Factory implements ILinkFactory {

		@Override
		public InteractorWidgetLink createLink(Interactor interactor, String fieldName, HashMap<String, String> attributes) {
			Widget widget = new FlexGrid();
			return new PassiveInteractorLink(interactor, fieldName, widget, attributes);
		}
		
	}
	

	
	public static class GridItem extends EnabledHorizontalPanel {
		
		public static class Factory implements ILinkFactory {

			@Override
			public InteractorWidgetLink createLink(Interactor interactor, String fieldName, HashMap<String, String> attributes) {
				Widget widget = new GridItem(attributes.get("colSpan"));
				return new PassiveInteractorLink(interactor, fieldName, widget, attributes);
			}
			
		}
		
		private final Integer colSpan;

		public GridItem(String colSpan) {
			this.colSpan = colSpan != null ? Integer.valueOf(colSpan) : null;
		}
		
	}
	
	public static class GridRow extends Panel {

		public static class Factory implements ILinkFactory {

			@Override
			public InteractorWidgetLink createLink(Interactor interactor, String fieldName, HashMap<String, String> attributes) {
				Widget widget = new GridRow();
				return new PassiveInteractorLink(interactor, fieldName, widget, attributes);
			}
			
		}
		

		private int row = -1;
		private int column = 0;
		private FlexGrid flexGrid;

		public GridRow() {
		}
		
		public void setFlexGrid(FlexGrid flexGrid) {
			this.flexGrid = flexGrid;
			row = flexGrid.getRowCount();
		}
		
		@Override
		public void add(Widget w) {
			if (w instanceof GridItem) {
				Integer colSpan = ((GridItem)w).colSpan;
				if (colSpan != null) {
					flexGrid.getFlexCellFormatter().setColSpan(row, column, colSpan);
				}
			}
			flexGrid.setWidget(row, column, w);
			
			column++;
		}
		
		

		@Override
		public void clear() {
			flexGrid.removeRow(row);
		}

		@Override
		public Iterator<Widget> iterator() {
			return null;
		}

		@Override
		public boolean remove(Widget w) {
			return flexGrid.remove(w);
		}

		@Override
		public Widget asWidget() {
			return this;
		}
	}
	

	@Override
	public void add(Widget child) {
		if (child instanceof GridRow) {
			((GridRow)child).setFlexGrid(this);
		} else {
			super.add(child);
		}
	}

	@Override
	public void add(IsWidget child) {
		if (child instanceof GridRow) {
			((GridRow)child).setFlexGrid(this);
		} else {
			super.add(child);
		}
	}

	
	
}

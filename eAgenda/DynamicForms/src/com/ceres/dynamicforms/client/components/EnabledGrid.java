package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class EnabledGrid extends Grid implements HasEnabled {
	private boolean enabled = true;

	public EnabledGrid(int rows, int columns) {
		super(rows, columns);
	}

	@Override
	public boolean isEnabled() {
		return this.enabled ;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getCellCount(row); column++) {
				Widget w = getWidget(row, column);
				if (w instanceof HasEnabled) {
					((HasEnabled)w).setEnabled(enabled);
				}
			}
		}
		
	}

	@Override
	public void setWidget(int row, int column, IsWidget w) {
		super.setWidget(row, column, w);
		if (w instanceof HasEnabled) {
			((HasEnabled)w).setEnabled(enabled);
		}
	}

	@Override
	public void setWidget(int row, int column, Widget w) {
		super.setWidget(row, column, w);
		if (w instanceof HasEnabled) {
			((HasEnabled)w).setEnabled(enabled);
		}
	}

	@Override
	public void add(Widget w) {
		super.add(w);
		if (w instanceof HasEnabled) {
			((HasEnabled)w).setEnabled(enabled);
		}
	}
	
}

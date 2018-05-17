package com.ceres.dynamicforms.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class HGapPanel extends FlexTable implements HasEnabled {

	private boolean isEnabled = true;
	
	public HGapPanel() {
		addStyleName("noBorderSpacing");
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
		if (getRowCount() > 0) {
			for (int col=0; col < getCellCount(0); col++) {
				Widget c = getWidget(0, col);
				if (c instanceof HasEnabled) {
					((HasEnabled)c).setEnabled(enabled);
				}
			}
		}
	}

	@Override
	public void add(Widget child) {
		add((IsWidget)child);
	}

	@Override
	public void add(IsWidget child) {
		if (child instanceof HasEnabled) {
			((HasEnabled)child).setEnabled(isEnabled);
		}
		int column = getRowCount() > 0 ? getCellCount(0) : 0; 
		setWidget(0, column, child);
		getFlexCellFormatter().setStyleName(0, column, "noPadding");
		child.asWidget().getElement().getStyle().setPaddingRight(3, Unit.PX);
	}
	
	

}

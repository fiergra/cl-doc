package com.ceres.dynamicforms.client;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleForm extends FlexTable {

	protected int row = 0;
	
	public SimpleForm() {
		this.setStyleName("simpleForm");
		
		getColumnFormatter().addStyleName(0, "formLabelColumn");
		getColumnFormatter().addStyleName(1, "formContentColumn");
	}
	
	public void addLine(String text, Widget widget) {
		Label label = new Label(text);
		label.addStyleName("formLabel");
		setWidget(row, 0, label);
		setWidget(row, 1, widget == null ? new Label("empty content") : widget);
		getRowFormatter().addStyleName(row, "formLine");
		row++;
	}

	
	@Override
	public void clear() {
		super.clear();
		removeAllRows();
		row = 0;
	}

	@Override
	public void add(Widget child) {
		if (child instanceof SimpleFormItem) {
			SimpleFormItem sf = (SimpleFormItem)child;
			sf.setSimpleForm(this);
			addLine(sf.getAttribute("label")/*application.getLabel(sf.label)*/, sf.content);
		} else {
			setWidget(row, 0, child);
			getFlexCellFormatter().setColSpan(row, 0, 2);
			getRowFormatter().addStyleName(row, "formLine");
			row++;
		}
	}
	
	

}

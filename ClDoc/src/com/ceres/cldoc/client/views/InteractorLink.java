package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.views.Form.DataType;
import com.google.gwt.user.client.ui.Widget;

public class InteractorLink {
	public String name;
	public Widget widget;
	public DataType dataType;
	public boolean isMandatory;

	public InteractorLink(String name, Widget widget, DataType dataType, boolean isMandatory) {
		this.name = name;
		this.widget = widget;
		this.dataType = dataType;
		this.isMandatory = isMandatory;
	}

}

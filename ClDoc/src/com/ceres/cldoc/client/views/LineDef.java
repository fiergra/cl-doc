package com.ceres.cldoc.client.views;

import java.util.HashMap;

import com.ceres.cldoc.client.views.Form.DataType;

public class LineDef {

	public final String label;
	public final String fieldName;
	public final DataType dataType;
	public HashMap<String, String> attributes;

	public LineDef(String label, String fieldName, DataType dataType) {
		this(label, fieldName, dataType, null);
	}
	
	public LineDef(String label, String fieldName, DataType dataType, HashMap<String, String> attributes) {
		this.label = label;
		this.fieldName = fieldName;
		this.dataType = dataType;
		this.attributes = attributes;
	}

}

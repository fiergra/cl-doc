package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class LayoutDefinition implements Serializable {

	private static final long serialVersionUID = 7581049677245652840L;

	public static final int FORM_LAYOUT = 1;
	public static final int PRINT_LAYOUT = 2;

	public Long id;
	public String name;
	public String xmlLayout;
	public Date validTo;
	public int type;
	
	private LayoutDefinition(){}

	public LayoutDefinition(int type, String name, String xmlLayout) {
		this(null, type, name, xmlLayout);
	}

	public LayoutDefinition(Long id, int type, String name, String xmlLayout) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.xmlLayout = xmlLayout;
	}
}

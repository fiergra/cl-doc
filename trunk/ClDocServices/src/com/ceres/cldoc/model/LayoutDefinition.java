package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class LayoutDefinition implements Serializable {

	private static final long serialVersionUID = 7581049677245652840L;

	public static final int FORM_LAYOUT = 1;
	public static final int PRINT_LAYOUT = 2;


	public Long id;
	public String xmlLayout;
	public Date validTo;
	public int type;

	public ActClass actClass;

	public LayoutDefinition(){}
	
	public LayoutDefinition(ActClass actClass, int type, String xmlLayout) {
		this(null, actClass, type, xmlLayout);
	}	
	
	public LayoutDefinition(Long id, ActClass actClass, int type, String xmlLayout) {
		this.id = id;
		this.type = type;
		this.actClass = actClass;
		this.xmlLayout = xmlLayout;
	}
}

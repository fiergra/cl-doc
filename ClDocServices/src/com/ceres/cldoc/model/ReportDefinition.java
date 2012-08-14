package com.ceres.cldoc.model;

import java.io.Serializable;

public class ReportDefinition implements Serializable {

	public ReportDefinition() {}

	public ReportDefinition(Catalog catalog) {
		this.id = catalog.id;
		this.name = catalog.shortText;
		this.xml = catalog.text;
	}
	
	private static final long serialVersionUID = 532614954546237265L;

	public String name;
	public long type;
	public String xml;
	public Long id;
}

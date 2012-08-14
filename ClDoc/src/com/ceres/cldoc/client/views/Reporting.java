package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Catalog;

public class Reporting extends ConfiguredTabPanel<ClDoc> {

	public Reporting(ClDoc clDoc) {
		super(clDoc, "CLDOC.MAIN.Reporting", clDoc);
	}

	@Override
	protected void addTab(ClDoc clDoc, Catalog catalog, ClDoc model) {
//		super.addTab(clDoc, catalog, model);
		add(new ReportingPanel(clDoc, catalog), catalog.text, false);
	}

	
	
}

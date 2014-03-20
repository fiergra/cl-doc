package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.ReportDefinition;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Reporting extends TabLayoutPanel  {

	private ClDoc clDoc;

	public Reporting(ClDoc clDoc) {
		super(2, Unit.EM);
		this.clDoc = clDoc;
		setup();
	}

	private void setup() {
		SRV.configurationService.listReportDefinitions(clDoc.getSession(), new DefaultCallback<List<ReportDefinition>>(clDoc, "list report defs") {

			@Override
			public void onResult(List<ReportDefinition> reportDefs) {
				for (ReportDefinition rd:reportDefs) {
					add(new ReportTab(clDoc, rd), rd.name);
				}
			}
		});
		
	}

	
	
}

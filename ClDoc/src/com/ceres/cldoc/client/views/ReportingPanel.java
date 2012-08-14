package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.ReportDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class ReportingPanel extends DockLayoutPanel {

	private final HorizontalPanel buttons;
	private ReportTab reportTab;
	
	public ReportingPanel(ClDoc clDoc, Catalog catalog) {
		super(Unit.PX);
		buttons = new HorizontalPanel();
		setupButtons();
		HorizontalPanel buttonsContainer = new HorizontalPanel();
		buttonsContainer.addStyleName("buttonsPanel"); 
		buttonsContainer.add(buttons);
		buttons.setSpacing(3);
		addNorth(buttonsContainer, 38);
//		add(reports);
		addReportTab(clDoc, catalog);
	}

	private void addReportTab(ClDoc clDoc, Catalog catalog) {
		reportTab = new ReportTab(clDoc, new ReportDefinition(catalog));
		add(reportTab);
	}

	private void setupButtons() {
		Image pbRefresh = new Image("icons/32/Button-Reload-icon.png");
		buttons.add(pbRefresh);
		pbRefresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				reportTab.execute();
			}
		});
		
		Image pbExcel = new Image("icons/32/Document-Microsoft-Excel-icon.png");
		buttons.add(pbExcel);
		pbExcel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=xsl&id=" + reportTab.getReportDefinition().id , "_blank", "");

				reportTab.execute();
			}
		});
		
	}

//	private void addReportTabs() {
//		SRV.configurationService.listReportDefinitions(clDoc.getSession(), new DefaultCallback<List<ReportDefinition>>(clDoc, "list reports") {
//
//			@Override
//			public void onSuccess(List<ReportDefinition> result) {
//				for (ReportDefinition rd:result) {
//					reports.add(new ReportTab(clDoc, rd), rd.name);
//				}
//			}
//		});
//	}
//
}

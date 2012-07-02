package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.ReportDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class ReportingPanel extends DockLayoutPanel {

	private final ClDoc clDoc;
	private final HorizontalPanel buttons;
	private final TabLayoutPanel reports;
	
	public ReportingPanel(ClDoc clDoc) {
		super(Unit.PX);
		this.clDoc = clDoc;
		buttons = new HorizontalPanel();
		setupButtons();
		reports = new TabLayoutPanel(2, Unit.EM);
		HorizontalPanel buttonsContainer = new HorizontalPanel();
		buttonsContainer.addStyleName("buttonsPanel"); 
		buttonsContainer.add(buttons);
		buttons.setSpacing(3);
		addNorth(buttonsContainer, 38);
		add(reports);
		addReportTabs();
	}

	private void setupButtons() {
		Image pbRefresh = new Image("icons/32/Button-Reload-icon.png");
		buttons.add(pbRefresh);
		pbRefresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ReportTab rt = (ReportTab) reports.getWidget(reports.getSelectedIndex());
				rt.execute();
			}
		});
		
		Image pbExcel = new Image("icons/32/Document-Microsoft-Excel-icon.png");
		buttons.add(pbExcel);
		pbExcel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ReportTab rt = (ReportTab) reports.getWidget(reports.getSelectedIndex());
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=xsl&id=" + rt.getReportDefinition().id , "_blank", "");

				rt.execute();
			}
		});
		
	}

	private void addReportTabs() {
		SRV.configurationService.listReportDefinitions(clDoc.getSession(), new DefaultCallback<List<ReportDefinition>>(clDoc, "list reports") {

			@Override
			public void onSuccess(List<ReportDefinition> result) {
				for (ReportDefinition rd:result) {
					reports.add(new ReportTab(clDoc, rd), rd.name);
				}
			}
		});
	}

}

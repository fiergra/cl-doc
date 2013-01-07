package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.ReportDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class ReportTab extends SplitLayoutPanel {

	private final FlexTable table = new FlexTable();
	private final ReportDefinition reportDefinition;
	private Act filters = new Act();
	private final ClDoc clDoc; 

	public ReportTab(ClDoc clDoc, ReportDefinition rd) {
		super();
		this.clDoc = clDoc;
		this.reportDefinition = rd;
		setup();
	}

	private void setup() {
		addWest(createReportControls(), 200);
		add(new ScrollPanel(table));
	}


	private HorizontalPanel setupButtons(final Form<IAct> rc) {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.addStyleName("buttonsPanel");
		HorizontalPanel buttons = new HorizontalPanel();
		buttonsPanel.add(buttons);
		Image pbRefresh = new Image("icons/32/Button-Reload-icon.png");
		pbRefresh.addStyleName("linkButton");
		buttons.add(pbRefresh);
		pbRefresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rc.fromDialog();
				execute();
			}
		});
		
		Image pbExcel = new Image("icons/32/Document-Microsoft-Excel-icon.png");
		buttons.add(pbExcel);
		pbExcel.addStyleName("linkButton");
		pbExcel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=xsl&id=" + getReportDefinition().id , "_blank", "");

				execute();
			}
		});
		
		return buttonsPanel;
	}


	private Widget createReportControls() {
		DockLayoutPanel reportControls = new DockLayoutPanel(Unit.EM);
		Form<IAct> rc = new Form<IAct>(clDoc, filters, new Runnable(){

			@Override
			public void run() {
				
			}});

		reportControls.addNorth(setupButtons(rc), 3);
		reportControls.add(rc);
		setupParams(rc);
		
		
		
		return reportControls;
	}

	private void setupParams(Form<IAct> rc) {
		if (reportDefinition.xml != null && reportDefinition.xml.length() > 0) {
			Document document = XMLParser.parse(reportDefinition.xml);
			NodeList params = document.getElementsByTagName("param");
			
			for (int i = 0; i < params.getLength(); i++) {
				Node item = params.item(i);
				Element child = item instanceof Element ? (Element)item : null;
				if (child != null) {
					addParam(rc, i, child);
				}
			}
		}
	}

	private void addParam(Form<IAct> rc, int i, Element child) {
		String fieldName = child.getAttribute("name");
		String labelText = child.getAttribute("label") == null ? fieldName : child.getAttribute("label");
		String sType = child.getAttribute("type");
		HashMap<String, String> attributes = asHashMap(child.getAttributes());
		attributes.put("width", "100%");
		rc.addLine(labelText, fieldName, rc.getDataType(sType), attributes);
	}

	private HashMap<String, String> asHashMap(NamedNodeMap nodeMap) {
		HashMap<String, String> attributes = new HashMap<String, String>();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node item = nodeMap.item(i);
			attributes.put(item.getNodeName(), item.getNodeValue());
		}
		return attributes;
	}

	void execute() {
		
		SRV.configurationService.executeReport(clDoc.getSession(), reportDefinition, filters, new DefaultCallback<List<HashMap<String,Serializable>>>(clDoc, "exec:") {

			@Override
			public void onSuccess(List<HashMap<String, Serializable>> result) {
				table.removeAllRows();
				Iterator<HashMap<String, Serializable>> iter = result.iterator();
				int row = 0;
				int column = 0;
				while (iter.hasNext()) {
					column = 0;
					HashMap<String, Serializable> next = iter.next();
					if (row == 0) {
						Iterator<Entry<String, Serializable>> eIter = next.entrySet().iterator();
						while (eIter.hasNext()) {
							Entry<String, Serializable> entry = eIter.next();
							Label headerText = new Label(entry.getKey());
							headerText.addStyleDependentName("tableHeader");
							table.setWidget(0, column, headerText);
							table.setWidget(1, column, new Label(entry.getValue() != null ? entry.getValue().toString() : "<null>"));
							column++;
						}
						row = 1;
					} else {
						Iterator<Serializable> vIter = next.values().iterator();
						while (vIter.hasNext()) {
							Serializable value = vIter.next();
							table.setWidget(row, column++, new Label(value != null ? value.toString() : "<null>"));
						}
					}
					RowFormatter rf = table.getRowFormatter();
					rf.addStyleName(row, "resultRow");
					if ((row - 1) % 2 == 0) {
						rf.addStyleName(row, "evenRow");
					}
					
					row++;
				}
				table.getRowFormatter().addStyleName(0, "tableHeader");
				if (column > 0) {
					table.getColumnFormatter().addStyleName(column - 1, "hundertPercentWidth");
				}
			}
		});
	}

	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}

}

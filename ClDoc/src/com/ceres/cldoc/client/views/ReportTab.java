package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.ReportDefinition;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ReportTab extends DockLayoutPanel {

	private final FlexTable table = new FlexTable();
	private final ReportDefinition reportDefinition;
	private final ClDoc clDoc; 

	public ReportTab(ClDoc clDoc, ReportDefinition rd) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.reportDefinition = rd;
		setup();
		execute();
	}

	private void setup() {
		add(new ScrollPanel(table));
	}

	void execute() {
		SRV.configurationService.executeReport(clDoc.getSession(), reportDefinition, new DefaultCallback<List<HashMap<String,Serializable>>>(clDoc, "exec:") {

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

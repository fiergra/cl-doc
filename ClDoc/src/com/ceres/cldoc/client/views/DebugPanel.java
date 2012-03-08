package com.ceres.cldoc.client.views;

import java.util.Date;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DebugPanel extends DockLayoutPanel implements LogOutput {

	private FlexTable logEntries = new FlexTable();
	
	public DebugPanel(ClDoc clDoc) {
		super(Unit.EM);
		
		clDoc.setLogOutput(this);
		
		log("debug", "start...");
		logEntries = new FlexTable();
		logEntries.addStyleName("logEntries");
		
		add(logEntries);
		
		log("debug", "debug panel created1");
		log("debug", "debug panel created2");
		log("debug", "debug panel created3");
	}

	@Override
	public void log(String title, String message) {
		logEntries.insertRow(0);
		logEntries.setWidget(0, 0, getTime());
		logEntries.setWidget(0, 1, new HTML("<b>" + title + "</b>"));
		logEntries.setWidget(0, 2, new HTML(message));
	}

	private Widget getTime() {
		Label l = new Label(DateTimeFormat.getFormat("HH:mm:ss.SS").format(new Date()));
		
		return l;
	}
	
}

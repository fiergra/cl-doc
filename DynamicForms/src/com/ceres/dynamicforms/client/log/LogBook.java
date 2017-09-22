package com.ceres.dynamicforms.client.log;

import java.util.Date;
import java.util.Iterator;

import com.ceres.dynamicforms.client.ResultCallback;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class LogBook extends FlexTable {
	
	public LogBook() {
		Log.register(new ResultCallback<LogEntry>() {
			
			@Override
			public void callback(LogEntry logEntry) {
				insertLine(logEntry);
			}
		});
		
		Iterator<LogEntry> entries = Log.getEntries();
		int row = 0;
		while (entries.hasNext()) {
			addLine(row++, entries.next());
		}
	}

	private DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM HH:mm:ss");
	
	private DateTimeFormat tf = DateTimeFormat.getFormat("HH:mm:ss");
	
	private void insertLine(LogEntry logEntry) {
		insertRow(0);
		addLine(0, logEntry);
	}

	private void addLine(int row, LogEntry logEntry) {
		int col = 0;
		setWidget(row, col++, new Label(logEntry.level.getName()));
		setWidget(row, col++, new Label(dtf.format(new Date(logEntry.time))));
		setWidget(row, col++, new Label(logEntry.text));
	}
}

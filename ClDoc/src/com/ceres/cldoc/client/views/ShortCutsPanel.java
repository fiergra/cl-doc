package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.ILogService;
import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.cldoc.model.Person;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ShortCutsPanel extends ClickableTable<LogEntry> {

	public ShortCutsPanel(final ClDoc clDoc) {
		super(clDoc, new ListRetrievalService<LogEntry>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<LogEntry>> callback) {
				SRV.actService.listRecent(clDoc.getSession(), callback);
			}
		}, new OnClick<LogEntry>() {

			@Override
			public void onClick(LogEntry le) {
				new MessageBox("Click", le.act != null ? le.act.className
						: le.logDate.toString(), MessageBox.MB_OK,
						MESSAGE_ICONS.MB_ICON_INFO).show();
			}
		}, true);
	}

	@Override
	public void addRow(FlexTable ft, int row, LogEntry le) {
		DateTimeFormat df = DateTimeFormat.getFormat("dd.MM.yyyy");
		String date = df.format(le.logDate);
		ft.setWidget(row, 0, new Label(date));
		if (le.act != null) {
			// date = df.format(le.act.date);
			ft.setWidget(row, 1, getTypeIcon(le.type));
			
			HTML html = new HTML("<b>" + le.act.className + "</b>");
			html.setWidth("100%");
			ft.setWidget(row, 2, html);
		}
		if (le.entity != null) {
			ft.setWidget(row, 3, new Label(((Person)le.entity).firstName));
			HTML html = new HTML("<b>" + ((Person)le.entity).lastName + "</b>");
			html.setWidth("100%");
			ft.setWidget(row, 4, html);
		}

	}

	private Widget getTypeIcon(int type) {
		String source = "icons/16/Document-icon.png";
		switch (type) {
		case ILogService.INSERT:
			source = "icons/16/Button-Add-01.png";
			break;
		case ILogService.VIEW:
			source = "icons/16/Document-Preview-01.png";
			break;
		case ILogService.UPDATE:
			source = "icons/16/Edit-Document-01.png";
			break;
		}
		Image image = new Image(source);
		image.setPixelSize(16, 16);
		return image;
	}

}

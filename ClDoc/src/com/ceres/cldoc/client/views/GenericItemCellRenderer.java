package com.ceres.cldoc.client.views;

import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class GenericItemCellRenderer extends AbstractCell<GenericItem> {

	@Override
	public void render(Context context, GenericItem value, SafeHtmlBuilder sb) {
		String sDate = value.getModified() != null ? DateTimeFormat.getFormat("dd.MM.yy").format(value.getModified()) : "--.--.----";
		String sTime = value.getModified() != null ? DateTimeFormat.getFormat("HH:mm").format(value.getModified()) : "--.--.----";
		sb.appendHtmlConstant(
						"<table class=\"historyEntry\" width=\"320px\"><tr>" +
						"<td width=\"20px\"><img src=\"" + getIcon(value) + "\"/></td>" +
						"<td width=\"150px\">" + value.getClassName() + "</td>" +
						"<td width=\"75px\">" + sDate + "</td>" +
						"<td width=\"75px\">" + sTime + "</td>" +
						"</tr></table>" );
	}

	private String getIcon(GenericItem value) {
		if (value.getClassName().equals("externalDoc")) {
			return "icons/16/Adobe-PDF-Document-icon.png";
		} else {
			return "icons/16/Document-icon.png";
		}
	}

}

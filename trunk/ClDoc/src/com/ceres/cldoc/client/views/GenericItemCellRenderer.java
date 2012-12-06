package com.ceres.cldoc.client.views;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class GenericItemCellRenderer extends AbstractCell<Act> {

	@Override
	public void render(Context context, Act value, SafeHtmlBuilder sb) {
		String sDate = value.date != null ? DateTimeFormat.getFormat("dd.MM.yy").format(value.date) : "--.--.----";
		String sTime = value.date != null ? DateTimeFormat.getFormat("HH:mm").format(value.date) : "--.--.----";
		
		sb.appendHtmlConstant(
						"<table class=\"historyEntry" + 
						(context.getIndex() % 2 == 1 ? " oddRow" : " evenRow") + "\" width=\"320px\" ><tr>" +
						"<td width=\"20px\"><img src=\"" + getIcon(value) + "\"/></td>" +
						"<td width=\"75px\">" + sDate + "</td>" +
						"<td width=\"150px\"><b>" + value.actClass.name + "</b></td>" +
						"</tr></table>" );
	}

	private String getIcon(Act value) {
		if (value.actClass.name.equals(ActClass.EXTERNAL_DOC.name)) {
			return "icons/16/Adobe-PDF-Document-icon.png";
		} else {
			return "icons/16/Document-icon.png";
		}
	}

}

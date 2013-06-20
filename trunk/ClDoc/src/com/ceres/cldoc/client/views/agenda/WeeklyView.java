package com.ceres.cldoc.client.views.agenda;

import java.util.Date;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

public class WeeklyView extends LayoutPanel {

	public WeeklyView(ClDoc clDoc, Date date) {
		super();
	}

	public void setDate(Date date) {
		int dayOfWeek = (date.getDay() - 1)  % 7;
		int width = this.getOffsetWidth();
		float dayWidth = width / 5;
		DateTimeFormat df = DateTimeFormat.getFormat("dd.MM.yyyy");
		Date start = new Date(date.getTime() - dayOfWeek * CalendarView.ONE_DAY);
		
		clear();
		for (int i = 0; i < 6; i++) {
			Label l = new Label(df.format(start));
			add(l);
			setWidgetLeftRight(l, dayWidth * i, Unit.PX, 0, Unit.PX);
			start.setTime(start.getTime() + CalendarView.ONE_DAY);
		}
	}
}

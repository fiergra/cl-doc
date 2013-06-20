package com.ceres.cldoc.client.views.agenda;

import java.util.Date;

import com.ceres.cldoc.client.ClDoc;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class CalendarView extends DockLayoutPanel {

	private final WeeklyView weeklyView;
	private final Date today = new Date();
	private final DateBox dateBox = new DateBox();
	
	public CalendarView(ClDoc clDoc) {
		super(Unit.EM);
		weeklyView = new WeeklyView(clDoc, today);
		addNorth(createCalendarHeader(clDoc), 3);
		add(weeklyView);
		weeklyView.setDate(today);
	}

	private Widget createCalendarHeader(ClDoc clDoc) {
		HorizontalPanel hp = new HorizontalPanel();
		Button pbPrev = new Button("<img src=\"icons/16/arrow-mini-left-icon.png\"/>");
		Button pbNext = new Button("<img src=\"icons/16/arrow-mini-right-icon.png\"/>");
		dateBox.setValue(today);
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT)));
		pbPrev.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				decrementDate();
			}
		});
		
		pbNext.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incrementDate();
			}
		});
		
		hp.add(pbPrev);
		hp.add(dateBox);
		hp.add(pbNext);
		
		return hp;
	}

	public static final long ONE_DAY = 1000l * 60l * 60l * 24l;
	public static final long ONE_WEEK = ONE_DAY * 7L;
	
	protected void incrementDate() {
		incrementDate(ONE_WEEK);
	}

	private void incrementDate(long offset) {
		Date date = dateBox.getValue();
		if (date == null) {
			date = new Date();
		}
		
		Date newDate = new Date(date.getTime() + offset);
		dateBox.setValue(newDate);
		weeklyView.setDate(newDate);
	}

	protected void decrementDate() {
		incrementDate(-ONE_WEEK);
	}

}

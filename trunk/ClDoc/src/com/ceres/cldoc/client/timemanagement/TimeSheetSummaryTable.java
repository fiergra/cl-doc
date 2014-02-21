package com.ceres.cldoc.client.timemanagement;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.timemanagement.TimeSheetModel.TimeSheetEntry;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class TimeSheetSummaryTable extends FlexTable {

	DateTimeFormat dtf = DateTimeFormat.getFormat("LLL yy");
	private TimeSheetModel model;
	
	public TimeSheetSummaryTable(ClDoc clDoc) {
	}
	
	public Date getDate() {
		return model.currentMonth;
	}
	
	protected Date getFirstOfMonth(Date start) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("MMyyyy");
		DateTimeFormat dtf2 = DateTimeFormat.getFormat("dMMyyyy");
		return dtf2.parse("1" + dtf.format(start));
	}


	public void setModel(final TimeSheetModel model) {
		this.model = model;
		dtf.format(model.currentMonth);
		removeAllRows();
		calculate();
	}

	private void calculate() {
		DateTimeFormat dtfTableHeader = DateTimeFormat.getFormat("MMMM");
		DateTimeFormat dtfRowHeader = DateTimeFormat.getFormat("dd., E");
		int month = model.currentMonth.getMonth();
		Date curDate = new Date(model.currentMonth.getTime());
		curDate.setDate(1);
		int row = 1;
		
		setWidget(0, 0, new Label(dtfTableHeader.format(model.currentMonth)));
		
		while (curDate.getMonth() == month) {
			TimeSheetEntry tse = model.get(curDate);
			Act leave = tse.leave; 
			if (leave != null) {
				if (leave.actClass.name.equals(TimeRegistration.ANNUAL_LEAVE_ACT)) {
					getRowFormatter().addStyleName(row, "leaveDay");
				} else {
					getRowFormatter().addStyleName(row, "sickLeaveDay");
				}

			} 
			Label dayLabel = new Label(dtfRowHeader.format(curDate));
			dayLabel.addStyleName("headerDate");
			if (TimeRegistration.isWeekEnd(curDate)) {
				dayLabel.addStyleName("weekendDate");
			}
			setWidget(row, 0, dayLabel);
			getRowFormatter().addStyleName(row, "timeSheetRow");
			List<Participation> participations = tse.participations;
			if (participations != null) {
				setWidget(row, 1, new Label(String.valueOf(participations.size())));
				setWidget(row, 2, new Label(String.valueOf(getDurationAsString(tse.durationIs))));
				setWidget(row, 3, new Label(String.valueOf(getDurationAsString(tse.durationShould))));
				setWidget(row, 4, new Label(String.valueOf(getDurationAsString(tse.durationIs - tse.durationShould))));
				getRowFormatter().addStyleName(row, "workingDate");
			} else {
				setWidget(row, 1, new Label("---"));
				setWidget(row, 2, new Label("---"));
			}

			CalendarUtil.addDaysToDate(curDate, 1);
			row++;
		}

//		List <Participation> participations = new ArrayList<Participation>();
//		for (Act act:result) {
//			Participation par = act.getParticipation(Participation.ADMINISTRATOR);
//			if (par != null && par.start != null && par.end != null && currentMonth.equals(dtf.format(par.start))) {
//				participations.add(par);
//			}
//		}
		
	}

	private int getDuration(List<Participation> participations) {
		int duration = 0;
		for (Participation p:participations) {
			duration += TimeRegistration.getDuration(p);
		}
		return duration;
	}

	private String getDurationAsString(int duration) {
		int hours = duration / 60;
		int minutes = duration % 60;
		return hours + ":" + minutes;
	}

	public void setOnClickDate(Runnable onClickDate) {
	}

	public void setWorkPattern(WorkPattern wp) {
		// TODO Auto-generated method stub
		
	}

}

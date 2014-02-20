package com.ceres.cldoc.client.timemanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.LeaveRegistration;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class TimeSheetSummaryTableOld extends FlexTable {

	DateTimeFormat dtf = DateTimeFormat.getFormat("LLL yy");
	private String currentMonth = "";
	private final ClDoc clDoc;
	private Date date;
	
	public TimeSheetSummaryTableOld (ClDoc clDoc) {
		this.clDoc = clDoc;
	}
	
	public Date getDate() {
		return date;
	}
	
	protected Date getFirstOfMonth(Date start) {
		DateTimeFormat dtf = DateTimeFormat.getFormat("MMyyyy");
		DateTimeFormat dtf2 = DateTimeFormat.getFormat("dMMyyyy");
		return dtf2.parse("1" + dtf.format(start));
	}


	public void setDate(final Date date) {
		this.date = date;
		SRV.actService.findByEntity(clDoc.getSession(), null, clDoc.getSession().getUser().getPerson(), Participation.ADMINISTRATOR.id, null, null, new DefaultCallback<List<Act>>(clDoc, "list acts by date") {

			@Override
			public void onSuccess(List<Act> result) {
				currentMonth = dtf.format(date);
				removeAllRows();
				calculate(date, result, getLeaves(result));
			}
		});
	}

	protected List<Act> getLeaves(List<Act> result) {
		List<Act> leaves = new ArrayList<Act>();
		for (Act a:result) {
			if (a.actClass.name.equals(TimeRegistration.ANNUAL_LEAVE_ACT) || a.actClass.name.equals(TimeRegistration.SICK_LEAVE_ACT)) {
				leaves.add(a);
			}
		}
		return leaves;
	}

	private List<List<Participation>> groupByDay(Date date, List<Act> result) {
		List<List<Participation>> grouped = new ArrayList<List<Participation>>(31);
		for (int i = 0; i < 31; i++) {
			grouped.add(null);
		}
		for (Act act:result) {
			if (act.actClass.name.equals(TimeRegistration.WORKINGTIME_ACT)) {
				Participation par = act.getParticipation(Participation.ADMINISTRATOR);
				
				if (par != null && par.start != null && par.end != null && currentMonth.equals(dtf.format(par.start))) {
					List<Participation> participations = getParticipations(grouped, par);
					participations.add(par);
				}
			}
		}

		return grouped;
	}
	
	private List<Participation> getParticipations(List<List<Participation>> grouped, Participation par) {
		int dayOfMonth = par.start.getDate();
		List<Participation> participations = grouped.get(dayOfMonth - 1);
		if (participations == null) {
			participations = new ArrayList<Participation>();
			grouped.set(dayOfMonth - 1, participations);
		}
		return participations;
	}

	private void calculate(Date date, List<Act> result, List<Act> leaves) {
		DateTimeFormat dtfTableHeader = DateTimeFormat.getFormat("MMMM");
		DateTimeFormat dtfRowHeader = DateTimeFormat.getFormat("dd., E");
		List<List<Participation>> grouped = groupByDay(date, result);
		int month = date.getMonth();
		Date curDate = new Date(date.getTime());
		curDate.setDate(1);
		int row = 1;
		
		setWidget(0, 0, new Label(dtfTableHeader.format(date)));
		
		while (curDate.getMonth() == month) {
			Act leave = LeaveRegistration.isLeave(leaves, curDate); 
			if (leave != null) {
				if (leave.actClass.name.equals(TimeRegistration.ANNUAL_LEAVE_ACT)) {
					getRowFormatter().addStyleName(row, "leaveDay");
				} else {
					getRowFormatter().addStyleName(row, "sickLeaveDay");
				}

			} 
			Label dayLabel = new Label(dtfRowHeader.format(curDate));
			dayLabel.addStyleName("headerDate");
			if (isWeekEnd(curDate)) {
				dayLabel.addStyleName("weekendDate");
			}
			setWidget(row, 0, dayLabel);
			getRowFormatter().addStyleName(row, "timeSheetRow");
			List<Participation> participations = grouped.get(row-1);
			if (participations != null) {
				setWidget(row, 1, new Label(String.valueOf(participations.size())));
				setWidget(row, 2, new Label(String.valueOf(getDurationAsString(getDuration(participations)))));
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

	private boolean isWeekEnd(Date curDate) {
		int weekDay = curDate.getDay();
		return  weekDay == 0 || weekDay == 6;
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

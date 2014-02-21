package com.ceres.cldoc.client.timemanagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.client.views.LeaveRegistration;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class TimeSheetModel  {
	public static class TimeSheetEntry {
		public TimeSheetEntry(Date date, int durationShould) {
			this.date = date;
			this.durationShould = durationShould;
		}
		public final Date date;
		public Act leave;
		
		List<Participation> participations = new ArrayList<Participation>();
		int durationIs = 0;
		public final int durationShould;
		
		public void add(Participation par) {
			durationIs  += TimeRegistration.getDuration(par);
			participations.add(par);
		}
	};
	
	public final Date currentMonth;
	private List<TimeSheetEntry> grouped;
	public final float carryOver;
	public final WorkPattern workPattern;
	
	public TimeSheetModel(WorkPattern workPattern, Date month, float carryOver) {
		this.workPattern = workPattern;
		this.currentMonth = month;
		this.carryOver = carryOver;
	}
	
	public TimeSheetEntry get(Date date){
		return grouped.get(date.getDate() - 1);
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


	public void calculate(List<Act> acts, List<Act> leaves) {
		HashMap<Integer, TimeSheetEntry> byDate = new HashMap<Integer, TimeSheetModel.TimeSheetEntry>();
		grouped = new ArrayList<TimeSheetEntry>(31);
		int month = currentMonth.getMonth();
		Date curDate = new Date(currentMonth.getTime());
		curDate.setDate(1);
		int row = 1;
		
		while (curDate.getMonth() == month) {
			TimeSheetEntry tse = byDate.get(curDate.getDate());
			int normalWorkingTime = (int) ((workPattern.weeklyHours * 60) / 5);
			if (tse == null) {
				tse = new TimeSheetEntry(curDate, normalWorkingTime);
				tse.leave = LeaveRegistration.isLeave(leaves, curDate);
				byDate.put(curDate.getDate(), tse);
				grouped.add(tse);
			}
			CalendarUtil.addDaysToDate(curDate, 1);
			row++;
		}

		for (Act act:acts) {
			if (act.actClass.name.equals(TimeRegistration.WORKINGTIME_ACT)) {
				Participation par = act.getParticipation(Participation.ADMINISTRATOR);
				
				if (par != null && par.start != null && par.end != null && currentMonth.getMonth() == par.start.getMonth()) {
					TimeSheetEntry tse = byDate.get(par.start.getDate());
					tse.add(par);
				}
			}
		}
	}
	

}

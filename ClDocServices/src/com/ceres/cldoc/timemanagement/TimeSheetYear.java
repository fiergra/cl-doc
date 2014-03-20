package com.ceres.cldoc.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;

public class TimeSheetYear extends SimpleTimeSheetElement {

	private static final long serialVersionUID = -1017251656742685721L;
	private int baseEntitlement;
	private int year;

	public TimeSheetYear() {}

	public TimeSheetYear(Date year, int baseEntitlement) {
		super(null, year);
		this.baseEntitlement = baseEntitlement;
		this.year = year.getYear() + 1900;
	}

	
	@Override
	public int getQuota() {
		return 0;
	}


	public float getAbsenceBalance() {
		return getLeaveEntitlement() - getAnnualLeaveDays();
	}

	public float getLeaveEntitlement() {
		float leaveEntitlement = 0f;
		for (TimeSheetElement tsm:getChildren()) {
			leaveEntitlement += ((TimeSheetMonth)tsm).getLeaveEntitlement(baseEntitlement);
		}
		return leaveEntitlement;
	}


	@Override
	public String toString() {
		return "YEAR: " + year + " (" + getLeaveEntitlement() + " - " + getAnnualLeaveDays() + " = " + getAbsenceBalance() + "): " + super.toString();
	}

	private boolean isLeave(Act act) {
		return act.actClass.name.equals(ITimeManagementService.ANNUAL_LEAVE_ACT) || act.actClass.name.equals(ITimeManagementService.SICK_LEAVE_ACT);
	}
	
	private AbsenceType getAbsenceType(Act act) {
		return isLeave(act) ? (act.actClass.name.equals(ITimeManagementService.ANNUAL_LEAVE_ACT) ? AbsenceType.HOLIDAY : AbsenceType.SICK) : AbsenceType.NONE;
	}
	
	public void add(Act act) {
		if (isLeave(act)) {
			addLeave(act);
		} else {
			addPresence(act);
		}
	}

	
	private Integer ldate(Date d) {
		return (d.getYear() + 1900) * 10000 + d.getMonth() * 100 + d.getDate();
	}

	private void addLeave(Act act) {
		Participation p = act.getParticipation(Participation.ADMINISTRATOR); 
		Date start = p.start;
		Date end = p.end;
		if (start != null && end != null && (start.getYear() + 1900) <= year && (end.getYear() + 1900) >= year) {
			int month = start.getMonth();
			int day = start.getDate() - 1;
			while (month <= end.getMonth()) {
				TimeSheetMonth tsm = (TimeSheetMonth) getChildren().get(month);
				TimeSheetDay tsd = (TimeSheetDay) tsm.getChildren().get(day);
				while (day < tsm.getChildren().size() && ldate(tsd.getDate()).compareTo(ldate(end)) <= 0) {
					if (!tsd.isAbsent() || tsd.getAbsence().actClass.name.equals(ITimeManagementService.ANNUAL_LEAVE_ACT)) {
						tsd.setAbsence(act);
					}
					tsd = (TimeSheetDay) tsm.getChildren().get(day++);
				}
				day = 0;
				month++;
			}
		}
	}
	
	private void addPresence(Act act) {
		Participation p = act.getParticipation(Participation.ADMINISTRATOR); 
		Date start = p.start;
		Date end = p.end;
		
		if (start != null && end != null && year == (start.getYear() + 1900)) {
			int month = start.getMonth();
			TimeSheetMonth tsm = (TimeSheetMonth) getChildren().get(month);
			int day = start.getDate() - 1;
			TimeSheetDay tsd = (TimeSheetDay) tsm.getChildren().get(day);
			tsd.add(act);
		}
	}


	public int getYear() {
		return year;
	}
	
}

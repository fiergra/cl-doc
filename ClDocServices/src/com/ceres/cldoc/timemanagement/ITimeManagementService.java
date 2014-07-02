package com.ceres.cldoc.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.Session;

public interface ITimeManagementService {
	public static final String WORKINGTIME_ACT = "WorkingTime";
	public static final String ANNUAL_LEAVE_ACT = "AnnualLeave";
	public static final String SICK_LEAVE_ACT = "SickLeave";
	public static final String HALFDAY_START = "halfDayStart";
	public static final String HALFDAY_END = "halfDayEnd";

	WorkPattern getWorkPattern(Session session, Entity person);
	WorkPattern getWorkPattern(Session session, Entity person, Date referenceDate);
	TimeSheetYear loadTimeSheetYear(Session session, Entity person, int year);
	void setWorkPattern(Session session, Person person, Entity wp, Date startFromMonth);
	byte[] exportXLS(Session session, Long valueOf);
}

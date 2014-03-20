package com.ceres.cldoc.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.core.ISession;

public interface ITimeManagementService {
	public static final String WORKINGTIME_ACT = "WorkingTime";
	public static final String ANNUAL_LEAVE_ACT = "AnnualLeave";
	public static final String SICK_LEAVE_ACT = "SickLeave";
	
	WorkPattern getWorkPattern(ISession session, Entity person);
	WorkPattern getWorkPattern(ISession session, Entity person, Date referenceDate);
	TimeSheetYear loadTimeSheetYear(ISession session, Entity person, int year);
	void setWorkPattern(ISession session, Person person, Entity wp, Date startFromMonth);
}

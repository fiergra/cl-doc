package com.ceres.cldoc.client.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.cldoc.Session;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("timemanagement")
public interface TimeManagementService extends RemoteService {
	WorkPattern getWorkPattern(Session session);
	WorkPattern getWorkPattern(Session session, Person person);
	void setWorkPattern(Session session, Person person, Entity wp, Date startFromMonth);
	TimeSheetYear loadTimeSheetYear(Session session, Person person, int year);
}

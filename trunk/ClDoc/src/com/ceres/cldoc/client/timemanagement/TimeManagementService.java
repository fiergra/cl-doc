package com.ceres.cldoc.client.timemanagement;

import java.util.Date;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.cldoc.model.ISession;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("timemanagement")
public interface TimeManagementService extends RemoteService {
	WorkPattern getWorkPattern(ISession session);
	WorkPattern getWorkPattern(ISession session, Person person);
	void setWorkPattern(ISession session, Person person, Entity wp, Date startFromMonth);
	TimeSheetYear loadTimeSheetYear(ISession session, Person person, int year);
}

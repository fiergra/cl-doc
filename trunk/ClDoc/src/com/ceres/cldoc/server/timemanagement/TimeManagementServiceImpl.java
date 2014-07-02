package com.ceres.cldoc.server.timemanagement;

import java.util.Date;

import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.timemanagement.TimeManagementService;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.TimeSheetYear;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.cldoc.Session;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TimeManagementServiceImpl extends RemoteServiceServlet implements
		TimeManagementService {

	@Override
	public WorkPattern getWorkPattern(Session session) {
		return Locator.getTimeManagementService().getWorkPattern(session, (Person) session.getUser().getPerson());
	}

	@Override
	public WorkPattern getWorkPattern(Session session, Person person) {
		return Locator.getTimeManagementService().getWorkPattern(session, person);
	}

	@Override
	public TimeSheetYear loadTimeSheetYear(Session session, Person person, int year) {
		return Locator.getTimeManagementService().loadTimeSheetYear(session, person, year);
	}

	@Override
	public void setWorkPattern(Session session, Person person, Entity wp, Date startFromMonth) {
		Locator.getTimeManagementService().setWorkPattern(session, person, wp, startFromMonth);
	}


}

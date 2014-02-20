package com.ceres.cldoc.server.timemanagement;

import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.timemanagement.TimeManagementService;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.core.ISession;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TimeManagementServiceImpl extends RemoteServiceServlet implements
		TimeManagementService {

	@Override
	public WorkPattern getWorkPattern(ISession session) {
		return Locator.getTimeManagementService().getWorkPattern(session, (Person) session.getUser().getPerson());
	}

	@Override
	public WorkPattern getWorkPattern(ISession session, Person person) {
		return Locator.getTimeManagementService().getWorkPattern(session, person);
	}


}

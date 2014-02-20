package com.ceres.cldoc.client.timemanagement;

import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.timemanagement.WorkPattern;
import com.ceres.core.ISession;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("timemanagement")
public interface TimeManagementService extends RemoteService {
	WorkPattern getWorkPattern(ISession session);
	WorkPattern getWorkPattern(ISession session, Person person);
}

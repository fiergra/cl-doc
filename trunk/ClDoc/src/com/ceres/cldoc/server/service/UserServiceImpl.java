package com.ceres.cldoc.server.service;

import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.UserService;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {
	@Override
	public Session login(String userName, String password) {
		Session session = new Session();
		return Locator.getUserService().login(session, userName, password);
	}

	@Override
	public void register(Person person, String userName, String password) {
		Session session = new Session();
		Locator.getUserService().register(session, person, userName, password);
	}

	@Override
	public long setPassword(Session session, User user, String password1, String password2) {
		return Locator.getUserService().setPassword(session, user, password1, password2);
	}
}

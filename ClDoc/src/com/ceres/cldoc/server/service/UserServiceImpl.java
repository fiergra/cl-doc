package com.ceres.cldoc.server.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.UserService;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Organisation;
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
		session = Locator.getUserService().login(session, userName, password);
		HttpSession httpSession = getThreadLocalRequest().getSession();
		httpSession.setAttribute(CLDOC_SESSION, session);
		return session;
	}

	@Override
	public void register(Person person, Organisation organisation, String userName, String password) {
		Session session = new Session();
		Locator.getUserService().register(session, person, organisation, userName, password);
	}

	@Override
	public long setPassword(Session session, User user, String password1, String password2) {
		return Locator.getUserService().setPassword(session, user, password1, password2);
	}

	@Override
	public List<User> listUsers(Session session, String filter) {
		return Locator.getUserService().listUsers(session, filter);
	}

	@Override
	public void addRole(Session session, User user, Catalog role) {
		Locator.getUserService().addRole(session, user, role);
	}

	@Override
	public void removeRole(Session session, User user, Catalog role) {
		Locator.getUserService().removeRole(session, user, role);
	}
}

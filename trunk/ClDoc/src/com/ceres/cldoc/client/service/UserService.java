package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.ceres.core.ISession;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
	public static final String CLDOC_SESSION = "cldoc-session";
	void register(Person person, Organisation organisation, String userName, String password);
	ISession login(String userName, String password);
	long setPassword(ISession session, User user, String password1, String password2);
	List<User> listUsers(ISession session, String filter);
	void addRole(ISession session, User user, Catalog role);
	void removeRole(ISession session, User user, Catalog role);
}

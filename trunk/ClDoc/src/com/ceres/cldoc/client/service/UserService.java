package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
	public static final String CLDOC_SESSION = "cldoc-session";
	void register(Person person, Entity organisation, String userName, String password);
	Session login(String userName, String password);
	long setPassword(Session session, User user, String password1, String password2);
	List<User> listUsers(Session session, String filter);
	void addRole(Session session, User user, Catalog role);
	void removeRole(Session session, User user, Catalog role);
}

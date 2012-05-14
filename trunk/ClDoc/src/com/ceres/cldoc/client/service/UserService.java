package com.ceres.cldoc.client.service;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService {
	void register(Person person, Organisation organisation, String userName, String password);
	Session login(String userName, String password);
	long setPassword(Session session, User user, String password1, String password2);
}

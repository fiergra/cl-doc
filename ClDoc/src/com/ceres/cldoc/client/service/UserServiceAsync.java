package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.ceres.core.ISession;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserServiceAsync {
	void setPassword(ISession session, User user, String password1, String password2, AsyncCallback<Long> callback);
	void register(Person person, Organisation organisation, String userName, String password, AsyncCallback<Void> callback);
	void login(String userName, String password, AsyncCallback<ISession> callback);
	void listUsers(ISession session, String text, AsyncCallback<List<User>> callback);
	void addRole(ISession session, User user, Catalog role, AsyncCallback<Void> callback);
	void removeRole(ISession session, User user, Catalog role, AsyncCallback<Void> callback);
}

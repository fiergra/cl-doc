package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserServiceAsync {
	void setPassword(Session session, User user, String password1, String password2, AsyncCallback<Long> callback);
	void register(Person person, Entity organisation, String userName, String password, AsyncCallback<Void> callback);
	void login(String userName, String password, AsyncCallback<Session> callback);
	void listUsers(Session session, String text, AsyncCallback<List<User>> callback);
	void addRole(Session session, User user, Catalog role, AsyncCallback<Void> callback);
	void removeRole(Session session, User user, Catalog role, AsyncCallback<Void> callback);
}

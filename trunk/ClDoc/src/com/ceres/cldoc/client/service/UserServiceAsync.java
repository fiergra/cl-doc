package com.ceres.cldoc.client.service;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UserServiceAsync {
	void setPassword(Session session, User user, String password1, String password2, AsyncCallback<Long> callback);
	void register(Person person, Organisation organisation, String userName, String password, AsyncCallback<Void> callback);
	void login(String userName, String password, AsyncCallback<Session> callback);
}

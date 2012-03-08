package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HumanBeingServiceAsync {
	void search(Session session, String criteria, AsyncCallback<List<Person>> callback);
	void delete(Session session, Person person, AsyncCallback<Void> defaultCallBack);
	void save(Session session, Person person, AsyncCallback<Person> callback);
	void findById(Session session, long id, AsyncCallback<Person> callback);
	void findByAssignment(Session session, String criteria, String roleCode, AsyncCallback<List<Person>> callback);
}

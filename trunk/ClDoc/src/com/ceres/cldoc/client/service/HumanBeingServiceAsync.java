package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.ISession;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HumanBeingServiceAsync {
	void search(ISession session, String criteria, AsyncCallback<List<Person>> callback);
	void delete(ISession session, Person person, AsyncCallback<Void> defaultCallBack);
	void save(ISession session, Person person, AsyncCallback<Person> callback);
	void findById(ISession session, long id, AsyncCallback<Person> callback);
	void findByAssignment(ISession session, String criteria, String roleCode, AsyncCallback<List<Person>> callback);
	void getUniqueId(ISession session, AsyncCallback<Long> callback);
}

package com.ceres.cldoc.client.service;

import com.ceres.cldoc.shared.domain.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PersonServiceAsync {
	void delete(Person result, AsyncCallback<Void> defaultCallBack);
	void save(Person person, AsyncCallback<Person> callback);
}

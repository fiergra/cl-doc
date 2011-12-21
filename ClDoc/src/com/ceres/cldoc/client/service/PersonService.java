package com.ceres.cldoc.client.service;

import com.ceres.cldoc.shared.domain.Person;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("person")
public interface PersonService extends RemoteService {
	Person save(Person person);
	void delete(Person person);
}

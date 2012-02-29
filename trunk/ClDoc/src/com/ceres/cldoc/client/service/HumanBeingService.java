package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("humanbeing")
public interface HumanBeingService extends RemoteService {
	Person save(Session session, Person humanBeing);
	void delete(Session session, Person person);
	List<Person> search(Session session, String criteria);
	Person findById(Session session, long id);
}

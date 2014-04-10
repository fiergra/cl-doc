package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.ISession;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("humanbeing")
public interface HumanBeingService extends RemoteService {
	Long getUniqueId(ISession session);
	Person save(ISession session, Person humanBeing);
	void delete(ISession session, Person person);
	Person findById(ISession session, long id);

	List<Person> search(ISession session, String criteria);
	List<Person> findByAssignment(ISession session, String criteria, String roleCode);
}

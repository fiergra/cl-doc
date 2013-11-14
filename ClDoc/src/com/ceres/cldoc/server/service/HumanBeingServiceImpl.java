package com.ceres.cldoc.server.service;

import java.util.List;

import com.ceres.cldoc.IEntityService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.service.HumanBeingService;
import com.ceres.cldoc.model.Person;
import com.ceres.core.ISession;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HumanBeingServiceImpl extends RemoteServiceServlet implements
		HumanBeingService {

	private IEntityService getEntityService() {
		return Locator.getEntityService();
	}
	
	@Override
	public Person save(ISession session, Person person) {
		getEntityService().save(session, person);
		return person;
	}

	@Override
	public void delete(ISession session, Person person) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Person> search(ISession session, String criteria) {
		return getEntityService().search(session, criteria);
	}

	@Override
	public Person findById(ISession session, long id) {
		return getEntityService().load(session, id);
	}

	@Override
	public List<Person> findByAssignment(ISession session, String filter, String roleCode) {
		return getEntityService().load(session, filter, roleCode);
	}

	@Override
	public Long getUniqueId(ISession session) {
		return getEntityService().getUniqueId(session);
	}

}

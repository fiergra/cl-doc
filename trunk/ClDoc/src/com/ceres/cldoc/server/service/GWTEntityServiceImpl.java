package com.ceres.cldoc.server.service;

import java.util.List;

import com.ceres.cldoc.IEntityService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.GWTEntityService;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GWTEntityServiceImpl extends RemoteServiceServlet implements
		GWTEntityService {

	private IEntityService getEntityService() {
		return Locator.getEntityService();
	}
	
	@Override
	public Entity save(Session session, Entity entity) {
		getEntityService().save(session, entity);
		return entity;
	}

	@Override
	public Person save(Session session, Person person) {
		getEntityService().save(session, person);
		return person;
	}

	@Override
	public void delete(Session session, Person person) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Person> search(Session session, String criteria) {
		return getEntityService().search(session, criteria);
	}

	@Override
	public Person findById(Session session, long id) {
		return getEntityService().load(session, id);
	}

	@Override
	public List<Person> findByAssignment(Session session, String filter, String roleCode) {
		return getEntityService().load(session, filter, roleCode);
	}

	@Override
	public <T extends Entity> List<T> list(Session session,	Integer typeId) {
		return getEntityService().list(session, typeId);
	}

	@Override
	public List<EntityRelation> listRelations(Session session, Entity entity, boolean asSubject) {
		return getEntityService().listRelations(session, entity, asSubject);
	}

}

package com.ceres.cldoc.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.ceres.cldoc.IEntityService;
import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.GWTEntityService;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.util.Jdbc;
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
	}

	@Override
	public void delete(Session session, final Entity entity) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public <T> T execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("delete from EntityRelation where SubjectId = ? OR ObjectId = ?");
				s.setLong(1, entity.id);
				s.setLong(2, entity.id);
				s.execute();
				s.close();
				
				s = con.prepareStatement("delete from Participation where EntityId = ?");
				s.setLong(1, entity.id);
				s.execute();
				s.close();
				
				s = con.prepareStatement("delete from Entity where id = ?");
				s.setLong(1, entity.id);
				s.execute();
				s.close();
				
				return null;
			}
		});
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

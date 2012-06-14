package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("entity")
public interface GWTEntityService extends RemoteService {
	Entity save(Session session, Entity entity);
	Person save(Session session, Person humanBeing);
	
	void delete(Session session, Person person);
	void delete(Session session, Entity entity);
	
	<T extends Entity> T findById(Session session, long id);

	List<Entity> search(Session session, String criteria, int type);
	List<Person> search(Session session, String criteria);
	List<Person> findByAssignment(Session session, String criteria, String roleCode);
	
	<T extends Entity> List<T> list(Session session, Integer typeId);
	
	EntityRelation save(Session session, EntityRelation er);
	List<EntityRelation> listRelations(Session session,	Entity entity, boolean asSubject);
	void delete(Session session, EntityRelation er);

}

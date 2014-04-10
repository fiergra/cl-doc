package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.ISession;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("entity")
public interface GWTEntityService extends RemoteService {
	Entity save(ISession session, Entity entity);
	Person save(ISession session, Person humanBeing);
	
	void delete(ISession session, Person person);
	void delete(ISession session, Entity entity);
	
	<T extends Entity> T findById(ISession session, long id);

	List<Entity> search(ISession session, String criteria, int type);
	List<Person> search(ISession session, String criteria);
	List<Person> findByAssignment(ISession session, String criteria, String roleCode);
	
	<T extends Entity> List<T> list(ISession session, Integer typeId);
	
	EntityRelation save(ISession session, EntityRelation er);
	List<EntityRelation> listRelations(ISession session,	Entity entity, boolean asSubject, Catalog relationType);
	void delete(ISession session, EntityRelation er);

}

package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GWTEntityServiceAsync {
	void search(Session session, String criteria, AsyncCallback<List<Person>> callback);
	void search(Session session, String criteria, int type, AsyncCallback<List<Entity>> callback);
	
	void delete(Session session, Entity entity, AsyncCallback<Void> defaultCallBack);
	void delete(Session session, Person person, AsyncCallback<Void> defaultCallBack);

	void save(Session session, Entity entity, AsyncCallback<Entity> callback);
	void save(Session session, Person person, AsyncCallback<Person> callback);
	
	<T extends Entity> void findById(Session session, long id, AsyncCallback<T> callback);
	void findByAssignment(Session session, String criteria, String roleCode, AsyncCallback<List<Person>> callback);
	
	<T extends Entity> void list(Session session, Integer type, AsyncCallback<List<T>> callback);

	void save(Session session, EntityRelation er, AsyncCallback<EntityRelation> callback);
	void delete(Session session, EntityRelation er, AsyncCallback<Void> callback);
	void listRelations(Session session,	Entity entity, boolean asSubject, AsyncCallback<List<EntityRelation> > callback);
}

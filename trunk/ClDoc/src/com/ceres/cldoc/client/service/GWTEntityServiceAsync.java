package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.ISession;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GWTEntityServiceAsync {
	void search(ISession session, String criteria, AsyncCallback<List<Person>> callback);
	void search(ISession session, String criteria, int type, AsyncCallback<List<Entity>> callback);
	
	void delete(ISession session, Entity entity, AsyncCallback<Void> defaultCallBack);
	void delete(ISession session, Person person, AsyncCallback<Void> defaultCallBack);

	void save(ISession session, Entity entity, AsyncCallback<Entity> callback);
	void save(ISession session, Person person, AsyncCallback<Person> callback);
	
	<T extends Entity> void findById(ISession session, long id, AsyncCallback<T> callback);
	void findByAssignment(ISession session, String criteria, String roleCode, AsyncCallback<List<Person>> callback);
	
	<T extends Entity> void list(ISession session, Integer type, AsyncCallback<List<T>> callback);

	void save(ISession session, EntityRelation er, AsyncCallback<EntityRelation> callback);
	void delete(ISession session, EntityRelation er, AsyncCallback<Void> callback);
	void listRelations(ISession session,	Entity entity, boolean asSubject, Catalog relationType, AsyncCallback<List<EntityRelation> > callback);
}

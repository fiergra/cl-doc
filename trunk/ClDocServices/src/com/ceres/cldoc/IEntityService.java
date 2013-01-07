package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;

public interface IEntityService {
	void save(Session session, Entity entity);
	<T extends Entity> T load(Session session, long id);
	<T extends Entity> List<T> list(Session session, int typeId);
	List<Person>search(Session session, String filter);
	List<Entity> list(Session session, Integer typeId,	Long id);
	<T extends Entity> List<T> load(Session session, String filter, String roleCode);

	List<EntityRelation> listRelations(Session session,	Entity entity, boolean asSubject);
	List<Entity> list(Session session, String criteria, int type);
	EntityRelation save(Session session, EntityRelation er);
	void delete(Session session, EntityRelation er);
	long getUniqueId(Session session);

}

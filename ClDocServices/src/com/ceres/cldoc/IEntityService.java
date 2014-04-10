package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.ISession;

public interface IEntityService {
	void save(ISession session, Entity entity);
	<T extends Entity> T load(ISession session, long id);
	<T extends Entity> List<T> list(ISession session, int typeId);
	List<Person>search(ISession session, String filter);
	List<Entity> list(ISession session, Integer typeId,	Long id);
	<T extends Entity> List<T> load(ISession session, String filter, String roleCode);

	List<EntityRelation> listRelations(ISession session,	Entity entity, boolean asSubject, Catalog relationType);
	List<Entity> list(ISession session, String criteria, int type);
	EntityRelation save(ISession session, EntityRelation er);
	void delete(ISession session, EntityRelation er);
	long getUniqueId(ISession session);

}

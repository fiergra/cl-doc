package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Person;

public interface IEntityService {
	void save(Session session, AbstractEntity entity);
	<T extends AbstractEntity> T load(Session session, long id);
	List<Person>search(Session session, String filter);
}

package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.Entity;

public interface IActService {
	void save(Session session, Act act);
	Act load(Session session, long id);
	
	void registerActClass(Connection con, ActClass actClass)
			throws SQLException;
	
	List<ActClass> listClasses(Session session, String filter);
	List<Act> load(Session session, Entity entity, Long roleId);
	void delete(Session session, Act act);
	
	CatalogList loadCatalogList(Session session, long listId);
	void rebuildIndex(Session session);
	void save(Session session, Collection<Act> acts);
}
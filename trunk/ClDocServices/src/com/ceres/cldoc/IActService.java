package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.Entity;

public interface IActService {
	void save(Session session, Act act);
	Act load(Session session, long id);
	
	boolean registerActClass(Connection con, String className)
			throws SQLException;
	
	List<String> listClassNames(Session session, String filter);
	List<Act> load(Session session, Entity entity, Long roleId);
	void delete(Session session, Act act);
	
	CatalogList loadCatalogList(Session session, long listId);
}

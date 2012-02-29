package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.GenericItem;

public interface IGenericItemService {
	void save(Session session, GenericItem item);
	GenericItem load(Session session, long id);
	
	boolean registerItemClass(Connection con, String className)
			throws SQLException;
	
	List<GenericItem> load(Session session, AbstractEntity entity);
	
}

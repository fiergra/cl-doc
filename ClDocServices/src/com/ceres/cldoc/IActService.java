package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Attachment;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.Session;

public interface IActService {
	void save(Session session, Act act);
	Act load(Session session, long id);
	
	void registerActClass(Connection con, ActClass actClass)
			throws SQLException;
	
	List<ActClass> listClasses(Session session, String filter);
	List<Act> load(Session session, String className, Entity entity, Long roleId, Date dateFrom, Date dateTo);
//	void delete(Session session, Act act);
	
	CatalogList loadCatalogList(Session session, long listId);
//	void rebuildIndex(Session session);
	void save(Session session, Collection<Act> acts);
	
	List<Attachment> listAttachments(Session session, Act act);
	void saveAttachment(Session session, Attachment attachment);
	void deleteAttachment(Session session, Attachment attachment);
}

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
import com.ceres.core.IEntity;
import com.ceres.core.ISession;

public interface IActService {
	void save(ISession session, Act act);
	Act load(ISession session, long id);
	
	void registerActClass(Connection con, ActClass actClass)
			throws SQLException;
	
	List<ActClass> listClasses(ISession session, String filter);
	List<Act> load(ISession session, String className, IEntity entity, Long roleId, Date dateFrom, Date dateTo);
//	void delete(ISession session, Act act);
	
	CatalogList loadCatalogList(ISession session, long listId);
//	void rebuildIndex(ISession session);
	void save(ISession session, Collection<Act> acts);
	
	List<Attachment> listAttachments(ISession session, Act act);
	void saveAttachment(ISession session, Attachment attachment);
	void deleteAttachment(ISession session, Attachment attachment);
}

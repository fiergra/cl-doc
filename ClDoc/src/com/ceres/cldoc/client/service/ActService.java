package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Attachment;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.LogEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("act")
public interface ActService extends RemoteService {
	List<Act> findByEntity(Session session, Entity entity, Long roleId, Date date);
	List<LogEntry> listRecent(Session session);
	Act findById(Session session, long id);
	Act save(Session session, Act act);
	Collection<Act> save(Session session, Collection<Act> acts);
	String print(Session session, Act act);
	void delete(Session session, Act act);
	
	List<Attachment> listAttachments(Session session, Act act);
	Attachment saveAttachment(Session session, Attachment attachment);
	void deleteAttachment(Session session, Attachment attachment);

}

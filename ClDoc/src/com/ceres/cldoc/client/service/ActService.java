package com.ceres.cldoc.client.service;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Attachment;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.core.IEntity;
import com.ceres.core.ISession;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("act")
public interface ActService extends RemoteService {
	List<Act> findByEntity(ISession session, IEntity entity, Long roleId, Date dateFrom, Date dateTo);
	List<LogEntry> listRecent(ISession session);
	Act findById(ISession session, long id);
	Act save(ISession session, Act act);
	List<Act> save(ISession session, List<Act> acts);
	String print(ISession session, Act act);
//	void delete(ISession session, Act act);
	
	List<Attachment> listAttachments(ISession session, Act act);
	Attachment saveAttachment(ISession session, Attachment attachment);
	void deleteAttachment(ISession session, Attachment attachment);

}

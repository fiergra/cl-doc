package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LogEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("act")
public interface ActService extends RemoteService {
	List<Act> findByEntity(Session session, Entity entity, Long roleId);
	List<LogEntry> listRecent(Session session);
	Act findById(Session session, long id);
	Act save(Session session, Act act);
	Collection<Act> save(Session session, Collection<Act> acts);
	String print(Session session, Act act);
	void delete(Session session, Act act);
}

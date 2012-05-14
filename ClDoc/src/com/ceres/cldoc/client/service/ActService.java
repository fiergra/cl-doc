package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LogEntry;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("act")
public interface ActService extends RemoteService {
	List<Act> findByEntity(Session session, AbstractEntity entity);
	List<LogEntry> listRecent(Session session);
	Act findById(Session session, long id);
	Act save(Session session, Act act);
	String print(Session session, Act act);
	void delete(Session session, Act act);
}

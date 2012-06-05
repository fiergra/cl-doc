package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LogEntry;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ActServiceAsync {
	void listRecent(Session session, AsyncCallback<List<LogEntry>> callback);
	void delete(Session session, Act act, AsyncCallback<Void> defaultCallBack);
	void save(Session session, Act act, AsyncCallback<Act> callback);
	void print(Session session, Act act, AsyncCallback<String> callback);
	void findById(Session session, long id, AsyncCallback<Act> callback);
	void findByEntity(Session session, Entity entity, AsyncCallback<List<Act>> callback);
}

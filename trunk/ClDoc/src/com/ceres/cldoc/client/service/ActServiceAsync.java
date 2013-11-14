package com.ceres.cldoc.client.service;

import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Attachment;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.core.IEntity;
import com.ceres.core.ISession;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ActServiceAsync {
	void listRecent(ISession session, AsyncCallback<List<LogEntry>> callback);
//	void delete(ISession session, Act act, AsyncCallback<Void> defaultCallBack);
	void save(ISession session, Act act, AsyncCallback<Act> callback);
	void save(ISession session, List<Act> acts, AsyncCallback<List<Act>> callback);
	void print(ISession session, Act act, AsyncCallback<String> callback);
	void findById(ISession session, long id, AsyncCallback<Act> callback);
	void findByEntity(ISession session, IEntity e, Long roleId, Date date, AsyncCallback<List<Act>> callback);
	
	void listAttachments(ISession session, Act act, AsyncCallback<List<Attachment>> callback);
	void saveAttachment(ISession session, Attachment attachment, AsyncCallback<Attachment> callback);
	void deleteAttachment(ISession session, Attachment attachment, AsyncCallback<Void> callback);

}

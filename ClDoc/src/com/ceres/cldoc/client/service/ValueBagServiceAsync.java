package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.GenericItem;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ValueBagServiceAsync {
	void delete(Session session, GenericItem item, AsyncCallback<Void> defaultCallBack);
	void save(Session session, GenericItem item, AsyncCallback<GenericItem> callback);
	void findById(Session session, long id, AsyncCallback<GenericItem> callback);
	void findByEntity(Session session, AbstractEntity entity, AsyncCallback<List<GenericItem>> callback);
}

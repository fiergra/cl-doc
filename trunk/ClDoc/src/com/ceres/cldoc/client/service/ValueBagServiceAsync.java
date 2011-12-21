package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ValueBagServiceAsync {
	void delete(GenericItem item, AsyncCallback<Void> defaultCallBack);
	void save(GenericItem item, AsyncCallback<GenericItem> callback);
	void findById(Number number, AsyncCallback<GenericItem> callback);
	void findByEntity(RealWorldEntity entity, AsyncCallback<List<GenericItem>> callback);
}

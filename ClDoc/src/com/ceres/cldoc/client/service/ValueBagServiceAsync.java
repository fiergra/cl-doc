package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ValueBagServiceAsync {
	void delete(ValueBag person, AsyncCallback<Void> defaultCallBack);
	void save(ValueBag person, AsyncCallback<ValueBag> callback);
	void findById(Number number, AsyncCallback<ValueBag> callback);
	void findByEntity(RealWorldEntity entity, AsyncCallback<List<ValueBag>> callback);
}

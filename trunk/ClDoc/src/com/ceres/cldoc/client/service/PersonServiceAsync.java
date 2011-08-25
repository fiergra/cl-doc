package com.ceres.cldoc.client.service;

import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PersonServiceAsync {
	void delete(ValueBag result, AsyncCallback<Void> defaultCallBack);
	void save(ValueBag person, AsyncCallback<ValueBag> callback);
}

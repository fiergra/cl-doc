package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HumanBeingServiceAsync {
	void search(String criteria, AsyncCallback<List<HumanBeing>> callback);
	void findByString(String criteria, AsyncCallback<List<ValueBag>> callback);
	void delete(ValueBag person, AsyncCallback<Void> defaultCallBack);
	void save(ValueBag person, AsyncCallback<ValueBag> callback);
	void findById(Number number, AsyncCallback<ValueBag> callback);
	void findById(long id, AsyncCallback<HumanBeing> callback);
}

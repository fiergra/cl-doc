package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HumanBeingServiceAsync {
	void search(String criteria, AsyncCallback<List<HumanBeing>> callback);
	void delete(HumanBeing person, AsyncCallback<Void> defaultCallBack);
	void save(HumanBeing person, AsyncCallback<HumanBeing> callback);
	void findById(long id, AsyncCallback<HumanBeing> callback);
}

package com.ceres.cldoc.client.service;

import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("person")
public interface PersonService extends RemoteService {
	ValueBag save(ValueBag person);
	void delete(ValueBag person);
}

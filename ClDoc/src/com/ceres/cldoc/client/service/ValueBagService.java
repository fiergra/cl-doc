package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("valueBag")
public interface ValueBagService extends RemoteService {
	List<ValueBag> findByEntity(RealWorldEntity entity);
	ValueBag findById(Number id);
	ValueBag save(ValueBag humanBeing);
	void delete(ValueBag person);
}

package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.domain.INamedValueAccessor;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("valueBag")
public interface ValueBagService extends RemoteService {
	List<GenericItem> findByEntity(RealWorldEntity entity);
	GenericItem findById(Number id);
	GenericItem save(GenericItem humanBeing);
	void delete(GenericItem item);
}

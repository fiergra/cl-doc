package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.GenericItem;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("valueBag")
public interface ValueBagService extends RemoteService {
	List<GenericItem> findByEntity(Session session, AbstractEntity entity);
	GenericItem findById(Session session, long id);
	GenericItem save(Session session, GenericItem item);
	void delete(Session session, GenericItem item);
}

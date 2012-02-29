package com.ceres.cldoc.server.service;

import java.util.List;

import com.ceres.cldoc.IGenericItemService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.ValueBagService;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.GenericItem;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ValueBagServiceImpl extends RemoteServiceServlet implements
		ValueBagService {

	private IGenericItemService getGenericItemService() {
		return Locator.getGenericItemService();
	}
	
	@Override
	public List<GenericItem> findByEntity(Session session, AbstractEntity entity) {
		return getGenericItemService().load(session, entity);
	}

	@Override
	public GenericItem findById(Session session, long id) {
		return getGenericItemService().load(session, id);
	}

	@Override
	public GenericItem save(Session session, GenericItem item) {
		getGenericItemService().save(session, item);
		return item;
	}

	@Override
	public void delete(Session session, GenericItem item) {
		// TODO Auto-generated method stub
		
	}

}

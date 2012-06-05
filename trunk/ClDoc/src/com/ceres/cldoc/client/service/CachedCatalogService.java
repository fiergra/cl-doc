package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CachedCatalogService implements ConfigurationServiceAsync {

	@Override
	public void parse(Session session, String xml,
			AsyncCallback<LayoutElement> callback) {
		SRV.configurationService.parse(session, xml, callback);
	}

	@Override
	public void listClassNames(Session session, String filter,
			AsyncCallback<List<String>> callback) {
		SRV.configurationService.listClassNames(session, filter, callback);
	}

	@Override
	public void saveLayoutDefinition(Session session, int type, String className,
			String xmlLayoutDesc, AsyncCallback<Void> callback) {
		SRV.configurationService.saveLayoutDefinition(session, type, className, xmlLayoutDesc, callback);
	}

	private HashMap<String, LayoutDefinition> layouts = new HashMap<String, LayoutDefinition>();

	@Override
	public void getLayoutDefinition(Session session, String className, int typeId, AsyncCallback<LayoutDefinition> callback) {
		SRV.configurationService.getLayoutDefinition(session, className, typeId, callback);
	}

	@Override
	public void deleteLayoutDefinition(Session session, String className,
			AsyncCallback<Void> callback) {
		SRV.configurationService.deleteLayoutDefinition(session, className, callback);
	}

	HashMap <String, List <Catalog>> catalogsByParentCode = new HashMap<String, List<Catalog>>();
	HashMap <Long, Catalog> catalogById = new HashMap<Long, Catalog>();
	
	private void clearCache() {
		catalogsByParentCode.clear();
		catalogById.clear();
	}
	
	@Override
	public void delete(Session session, Catalog catalog,
			AsyncCallback<Void> callback) {
		clearCache();
		SRV.configurationService.delete(session, catalog, callback);
	}

	@Override
	public void save(Session session, Catalog catalog, AsyncCallback<Catalog> callback) {
		clearCache();
		SRV.configurationService.save(session, catalog, callback);
	}

	@Override
	public void listCatalogs(Session session, final String parentCode,
			final AsyncCallback<List<Catalog>> callback) {
		List<Catalog> result = catalogsByParentCode.get(parentCode);
		
		if (result != null) {
			callback.onSuccess(result);
		} else {
			SRV.configurationService.listCatalogs(session, parentCode, new AsyncCallback<List<Catalog>>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(List<Catalog> result) {
					catalogsByParentCode.put(parentCode, result);
					callback.onSuccess(result);
				}
			});
		}
	}

	@Override
	public void listCatalogs(Session session, Long parentId,
			AsyncCallback<List<Catalog>> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listCatalogs(Session session, Catalog parent,
			AsyncCallback<List<Catalog>> callback) {
		SRV.configurationService.listCatalogs(session, parent, callback);
		
	}

	@Override
	public void getCatalog(Session session, final long id,
			final AsyncCallback<Catalog> callback) {
		Catalog result = catalogById.get(id);
		
		if (result != null) {
			callback.onSuccess(result);
		} else {
			SRV.configurationService.getCatalog(session, id, new AsyncCallback<Catalog>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Catalog result) {
					catalogById.put(id, result);
					callback.onSuccess(result);
				}
			});
		}
	}

	@Override
	public void saveAll(Session session, Collection<Catalog> catalogs,
			AsyncCallback<Void> defaultCallback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addAssignment(Session session, Catalog catalog,
			Entity entity, AsyncCallback<Assignment> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listAssignments(Session session, Catalog catalog,
			AsyncCallback<List<Assignment>> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listAssignments(Session session, Entity entity,
			AsyncCallback<List<Assignment>> callback) {
		// TODO Auto-generated method stub
		
	}

}

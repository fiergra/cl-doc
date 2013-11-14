package com.ceres.cldoc.client.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.FileSystemNode;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.ReportDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.ceres.core.ISession;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CachedCatalogService implements ConfigurationServiceAsync {

	@Override
	public void parse(ISession session, String xml,
			AsyncCallback<LayoutElement> callback) {
		SRV.configurationService.parse(session, xml, callback);
	}

	@Override
	public void listClasses(ISession session, String filter,
			AsyncCallback<List<ActClass>> callback) {
		SRV.configurationService.listClasses(session, filter, callback);
	}

	@Override
	public void saveLayoutDefinition(ISession session, LayoutDefinition ld, AsyncCallback<LayoutDefinition> callback) {
		SRV.configurationService.saveLayoutDefinition(session, ld, callback);
	}

	private final HashMap<String, LayoutDefinition> layouts = new HashMap<String, LayoutDefinition>();

	@Override
	public void getLayoutDefinition(ISession session, String className, int typeId, AsyncCallback<LayoutDefinition> callback) {
		SRV.configurationService.getLayoutDefinition(session, className, typeId, callback);
	}

	@Override
	public void deleteLayoutDefinition(ISession session, String className,
			AsyncCallback<Void> callback) {
		SRV.configurationService.deleteLayoutDefinition(session, className, callback);
	}

	HashMap <String, List <Catalog>> catalogsByParentCode = new HashMap<String, List<Catalog>>();
	HashMap <Long, Catalog> catalogById = new HashMap<Long, Catalog>();
	HashMap <String, Catalog> catalogByCode = new HashMap<String, Catalog>();
	
	private void clearCache() {
		catalogsByParentCode.clear();
		catalogById.clear();
	}
	
	@Override
	public void delete(ISession session, Catalog catalog,
			AsyncCallback<Void> callback) {
		clearCache();
		SRV.configurationService.delete(session, catalog, callback);
	}

	@Override
	public void save(ISession session, Catalog catalog, AsyncCallback<Catalog> callback) {
		clearCache();
		SRV.configurationService.save(session, catalog, callback);
	}

	@Override
	public void listCatalogs(ISession session, final String parentCode,
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
					if (result != null) {
						for (Catalog c:result) {
							catalogByCode.put(c.parent != null ? c.parent.code + "." + c.code : c.code, c);
							catalogById.put(c.id, c);
						}
						catalogsByParentCode.put(parentCode, result);
					}
					callback.onSuccess(result);
				}
			});
		}
	}

	@Override
	public void listCatalogs(ISession session, Long parentId,
			AsyncCallback<List<Catalog>> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listCatalogs(ISession session, Catalog parent,
			AsyncCallback<List<Catalog>> callback) {
		SRV.configurationService.listCatalogs(session, parent, callback);
		
	}

	@Override
	public void getCatalog(ISession session, final long id,
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
	public void getCatalog(ISession session, final String code,
			final AsyncCallback<Catalog> callback) {
		Catalog result = catalogByCode.get(code);
		
		if (result != null) {
			callback.onSuccess(result);
		} else {
			SRV.configurationService.getCatalog(session, code, new AsyncCallback<Catalog>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Catalog result) {
					catalogByCode.put(code, result);
					callback.onSuccess(result);
				}
			});
		}
	}

	@Override
	public void saveAll(ISession session, Collection<Catalog> catalogs,
			AsyncCallback<Void> defaultCallback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addAssignment(ISession session, Catalog catalog,
			Entity entity, AsyncCallback<Assignment> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listAssignments(ISession session, Catalog catalog,
			AsyncCallback<List<Assignment>> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listAssignments(ISession session, Entity entity,
			AsyncCallback<List<Assignment>> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listReportDefinitions(ISession session,
			AsyncCallback<List<ReportDefinition>> callback) {
		SRV.configurationService.listReportDefinitions(session, callback);
	}

	@Override
	public void executeReport(ISession session, ReportDefinition rd, IAct filters,
			AsyncCallback<List<HashMap<String, Serializable>>> callback) {
		SRV.configurationService.executeReport(session, rd, filters, callback);
	}

	@Override
	public void loadReportDefinition(ISession session, Catalog catalog,
			AsyncCallback<ReportDefinition> callback) {
		SRV.configurationService.loadReportDefinition(session, catalog, callback);
	}

	@Override
	public void listLayoutDefinitions(ISession session, int typeId, Long entityType, Boolean isSingleton, AsyncCallback<List<LayoutDefinition>> defaultCallback) {
		SRV.configurationService.listLayoutDefinitions(session, typeId, entityType, isSingleton, defaultCallback);
	}


	@Override
	public void getDocArchivePath(AsyncCallback<String> callback) {
		SRV.configurationService.getDocArchivePath(callback);
	}

	@Override
	public void setDocArchivePath(ISession session, String path,
			AsyncCallback<Void> callback) {
		SRV.configurationService.setDocArchivePath(session, path, callback);
	}

	@Override
	public void listFiles(String directory,
			AsyncCallback<List<FileSystemNode>> callback) {
		SRV.configurationService.listFiles(directory, callback);
	}

	@Override
	public void set(ISession session, String name, String value, Entity entity,
			AsyncCallback<Void> callback) {
		SRV.configurationService.set(session, name, value, entity, callback);
	}

	@Override
	public void get(ISession session, String name, Entity entity, AsyncCallback<String> callback) {
		SRV.configurationService.get(session, name, entity, callback);
	}

}

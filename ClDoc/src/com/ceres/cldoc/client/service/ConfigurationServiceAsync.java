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

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConfigurationServiceAsync {
	void parse(ISession session, String xml, AsyncCallback<LayoutElement> callback);

	void listClasses(ISession session, String filter, AsyncCallback<List<ActClass>> callback);
	
	void saveLayoutDefinition(ISession session, LayoutDefinition ld, AsyncCallback<LayoutDefinition> callback);

	void getLayoutDefinition(ISession session, String className, int typeId, AsyncCallback<LayoutDefinition> callback);

	void listLayoutDefinitions(ISession session, int typeId, Long entityType, Boolean isSingleton, AsyncCallback<List<LayoutDefinition>> defaultCallback);

	void deleteLayoutDefinition(ISession session, String className, AsyncCallback<Void> callback);

	void delete(ISession session, Catalog catalog, AsyncCallback<Void> callback);

	void save(ISession session, Catalog catalog, AsyncCallback<Catalog> callback);

	void listCatalogs(ISession session, String parentCode, AsyncCallback<List<Catalog>> callback);
	
	void listCatalogs(ISession session, Long parentId, AsyncCallback<List<Catalog>> callback);
	
	void listCatalogs(ISession session, Catalog parent, AsyncCallback<List<Catalog>> callback);
	
	void getCatalog(ISession session, long id, AsyncCallback<Catalog> callback);

	void getCatalog(ISession session, String code, AsyncCallback<Catalog> callback);

	void saveAll(ISession session, Collection<Catalog> catalogs,
			AsyncCallback<Void> defaultCallback);
	
	void addAssignment(ISession session, Catalog catalog, Entity entity, AsyncCallback<Assignment> callback);
	void listAssignments(ISession session, Catalog catalog, AsyncCallback<List<Assignment>> callback);
	void listAssignments(ISession session, Entity entity, AsyncCallback<List<Assignment>> callback);

	void listReportDefinitions(ISession session, AsyncCallback<List<ReportDefinition>> callback);
	void loadReportDefinition(ISession session, Catalog catalog, AsyncCallback<ReportDefinition> callback);

	void executeReport(
			ISession session,
			ReportDefinition rd,
			IAct filters,
			AsyncCallback<List<HashMap<String, Serializable>>> callback);

	void getDocArchivePath(AsyncCallback<String> callback);
	void setDocArchivePath(ISession session, String path, AsyncCallback<Void> callback);

	void listFiles(String directory, AsyncCallback<List<FileSystemNode>> callback);
	void set(ISession session, String name, String value, Entity entity, AsyncCallback<Void> callback);
	void get(ISession session, String name, Entity entity, AsyncCallback<String> callback);

}

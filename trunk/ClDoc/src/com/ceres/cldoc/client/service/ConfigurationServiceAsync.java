package com.ceres.cldoc.client.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.ReportDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConfigurationServiceAsync {
	void parse(Session session, String xml, AsyncCallback<LayoutElement> callback);

	void listClasses(Session session, String filter, AsyncCallback<List<ActClass>> callback);
	
	void saveLayoutDefinition(Session session, LayoutDefinition ld, AsyncCallback<LayoutDefinition> callback);

	void getLayoutDefinition(Session session, String className, int typeId, AsyncCallback<LayoutDefinition> callback);

	void listLayoutDefinitions(Session session, int typeId, Long entityType, Boolean isSingleton, AsyncCallback<List<LayoutDefinition>> defaultCallback);

	void deleteLayoutDefinition(Session session, String className, AsyncCallback<Void> callback);

	void delete(Session session, Catalog catalog, AsyncCallback<Void> callback);

	void save(Session session, Catalog catalog, AsyncCallback<Catalog> callback);

	void listCatalogs(Session session, String parentCode, AsyncCallback<List<Catalog>> callback);
	
	void listCatalogs(Session session, Long parentId, AsyncCallback<List<Catalog>> callback);
	
	void listCatalogs(Session session, Catalog parent, AsyncCallback<List<Catalog>> callback);
	
	void getCatalog(Session session, long id, AsyncCallback<Catalog> callback);

	void getCatalog(Session session, String code, AsyncCallback<Catalog> callback);

	void saveAll(Session session, Collection<Catalog> catalogs,
			AsyncCallback<Void> defaultCallback);
	
	void addAssignment(Session session, Catalog catalog, Entity entity, AsyncCallback<Assignment> callback);
	void listAssignments(Session session, Catalog catalog, AsyncCallback<List<Assignment>> callback);
	void listAssignments(Session session, Entity entity, AsyncCallback<List<Assignment>> callback);

	void listReportDefinitions(Session session, AsyncCallback<List<ReportDefinition>> callback);
	void loadReportDefinition(Session session, Catalog catalog, AsyncCallback<ReportDefinition> callback);

	void executeReport(Session session, ReportDefinition rd, AsyncCallback<List<HashMap<String, Serializable>>> callback);

}

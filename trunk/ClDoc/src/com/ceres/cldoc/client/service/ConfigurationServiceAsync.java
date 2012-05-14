package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConfigurationServiceAsync {
	void parse(Session session, String xml, AsyncCallback<LayoutElement> callback);

//	void listChildren(String parent, AsyncCallback<List<String>> callback);
//
//	void getUploadUrl(AsyncCallback<String> callback);
//
	void listClassNames(Session session, String filter, AsyncCallback<List<String>> callback);
	
	void saveLayoutDefinition(Session session, int type, String className, String xmlLayoutDesc, AsyncCallback<Void> callback);

	void getLayoutDefinition(Session session, String className, int typeId, AsyncCallback<LayoutDefinition> callback);

	void deleteLayoutDefinition(Session session, String className, AsyncCallback<Void> callback);

	void delete(Session session, Catalog catalog, AsyncCallback<Void> callback);

	void save(Session session, Catalog catalog, AsyncCallback<Catalog> callback);

	void listCatalogs(Session session, String parentCode, AsyncCallback<Collection<Catalog>> callback);
	
	void listCatalogs(Session session, Long parentId, AsyncCallback<Collection<Catalog>> callback);
	
	void listCatalogs(Session session, Catalog parent, AsyncCallback<Collection<Catalog>> callback);
	
	void getCatalog(Session session, long id, AsyncCallback<Catalog> callback);

	void saveAll(Session session, Collection<Catalog> catalogs,
			AsyncCallback<Void> defaultCallback);
	
	void addAssignment(Session session, Catalog catalog, AbstractEntity entity, AsyncCallback<Assignment> callback);
	void listAssignments(Session session, Catalog catalog, AsyncCallback<List<Assignment>> callback);
	void listAssignments(Session session, AbstractEntity entity, AsyncCallback<List<Assignment>> callback);
}
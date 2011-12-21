package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.List;

import com.ceres.cldoc.shared.domain.Assignment;
import com.ceres.cldoc.shared.domain.Catalog;
import com.ceres.cldoc.shared.domain.FormClassDesc;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConfigurationServiceAsync {
	void parse(String xml, AsyncCallback<LayoutElement> callback);

	void listChildren(String parent, AsyncCallback<List<String>> callback);

	void getUploadUrl(AsyncCallback<String> callback);

	void listClasses(FormClassDesc parent, String filter, AsyncCallback<List<FormClassDesc>> callback);
	
	void saveLayoutDesc(String className, String xmlLayoutDesc, AsyncCallback<Void> callback);

	void getFormClassDesc(String className, AsyncCallback<FormClassDesc> callback);

	void deleteFormClassDesc(String className, AsyncCallback<Void> callback);

	void delete(Catalog catalog, AsyncCallback<Void> callback);

	void save(Catalog catalog, AsyncCallback<Catalog> callback);

	void listCatalogs(String parentCode, AsyncCallback<List<Catalog>> callback);
	
	void listCatalogs(Catalog parent, AsyncCallback<List<Catalog>> callback);
	
	void getCatalog(String code, AsyncCallback<Catalog> callback);

	void saveAll(Collection<Catalog> catalogs,
			AsyncCallback<Void> defaultCallback);
	
	void addAssignment(Catalog catalog, RealWorldEntity entity, AsyncCallback<Assignment> callback);
	void listAssignments(Catalog catalog, AsyncCallback<List<Assignment>> callback);
	void listAssignments(RealWorldEntity entity, AsyncCallback<List<Assignment>> callback);
}

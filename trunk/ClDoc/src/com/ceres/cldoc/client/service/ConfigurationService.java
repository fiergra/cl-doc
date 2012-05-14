package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.List;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("configuration")
public interface ConfigurationService extends RemoteService {
	LayoutElement parse(Session session, String xml);
//	List<String> listChildren(Session session, String parent);
//	String getUploadUrl();
	
	List <String> listClassNames(Session session, String filter);
//	List <LayoutDefinition> listLayoutDefinitions(Session session, String filter);
	void saveLayoutDefinition(Session session, int type, String formClass, String xmlLayout);
	LayoutDefinition getLayoutDefinition(Session session, String className, int typeId);
	void deleteLayoutDefinition(Session session, String className);
	
	void delete(Session session, Catalog catalog);
	Catalog save(Session session, Catalog catalog);
	void saveAll(Session session, Collection<Catalog> catalog);

	Collection<Catalog> listCatalogs(Session session, String parentCode);
	Collection<Catalog> listCatalogs(Session session, Long parentId);
	Collection<Catalog> listCatalogs(Session session, Catalog parent);
	Catalog getCatalog(Session session, long id);
	
	Assignment addAssignment(Session session, Catalog catalog, AbstractEntity entity);
	List<Assignment> listAssignments(Session session, Catalog catalog);
	List<Assignment> listAssignments(Session session, AbstractEntity entity);
}
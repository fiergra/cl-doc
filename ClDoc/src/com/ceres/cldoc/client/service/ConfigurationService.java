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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("configuration")
public interface ConfigurationService extends RemoteService {
	LayoutElement parse(ISession session, String xml);
//	List<String> listChildren(ISession session, String parent);
//	String getUploadUrl();
	
	List <ActClass> listClasses(ISession session, String filter);
//	List <LayoutDefinition> listLayoutDefinitions(ISession session, String filter);
	LayoutDefinition saveLayoutDefinition(ISession session, LayoutDefinition ld);
	LayoutDefinition getLayoutDefinition(ISession session, String className, int typeId);
	List<LayoutDefinition> listLayoutDefinitions(ISession session, int typeId, Long entityType, Boolean isSingleton);
	void deleteLayoutDefinition(ISession session, String className);
	
	void delete(ISession session, Catalog catalog);
	Catalog save(ISession session, Catalog catalog);
	void saveAll(ISession session, Collection<Catalog> catalog);

	List<Catalog> listCatalogs(ISession session, String parentCode);
	List<Catalog> listCatalogs(ISession session, Long parentId);
	List<Catalog> listCatalogs(ISession session, Catalog parent);
	Catalog getCatalog(ISession session, long id);
	Catalog getCatalog(ISession session, String code);
	
	Assignment addAssignment(ISession session, Catalog catalog, Entity entity);
	List<Assignment> listAssignments(ISession session, Catalog catalog);
	List<Assignment> listAssignments(ISession session, Entity entity);
	
	List<ReportDefinition> listReportDefinitions(ISession session);
	ReportDefinition loadReportDefinition(ISession session, Catalog catalog);
	List<HashMap<String, Serializable>> executeReport(ISession session, ReportDefinition rd, IAct filters);
	
//	String getLuceneIndexPath();
//	void setLuceneIndexPath(ISession session, String path);
	
	String getDocArchivePath();
	void setDocArchivePath(ISession session, String path);
	
	List<FileSystemNode> listFiles(String directory);
	void set(ISession session, String name, String value, Entity entity);
	String get(ISession session, String name, Entity entity);

}

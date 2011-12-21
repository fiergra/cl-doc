package com.ceres.cldoc.client.service;

import java.util.Collection;
import java.util.List;

import com.ceres.cldoc.shared.domain.Assignment;
import com.ceres.cldoc.shared.domain.Catalog;
import com.ceres.cldoc.shared.domain.FormClassDesc;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("configuration")
public interface ConfigurationService extends RemoteService {
	LayoutElement parse(String xml);
	List<String> listChildren(String parent);
	String getUploadUrl();
	
	List <FormClassDesc> listClasses(FormClassDesc parent, String filter);
	void saveLayoutDesc(String formClass, String xmlLayout);
	FormClassDesc getFormClassDesc(String className);
	void deleteFormClassDesc(String className);
	
	void delete(Catalog catalog);
	Catalog save(Catalog catalog);
	void saveAll(Collection<Catalog> catalog);
	List<Catalog> listCatalogs(String parentCode);
	List<Catalog> listCatalogs(Catalog parent);
	Catalog getCatalog(String code);
	
	Assignment addAssignment(Catalog catalog, RealWorldEntity entity);
	List<Assignment> listAssignments(Catalog catalog);
	List<Assignment> listAssignments(RealWorldEntity entity);
}

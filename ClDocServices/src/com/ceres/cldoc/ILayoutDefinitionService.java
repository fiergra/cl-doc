package com.ceres.cldoc;

import java.io.InputStream;
import java.util.List;

import com.ceres.cldoc.model.LayoutDefinition;

public interface ILayoutDefinitionService {
	void save(Session session, LayoutDefinition ld);
	LayoutDefinition load(Session session, String className, int typeId);
	List<LayoutDefinition> listLayoutDefinitions(Session session, String filter, Integer typeId);
	void delete(Session session, String className);
	
	byte[] exportZip(Session session);
	void importZip(Session session, InputStream in);
}

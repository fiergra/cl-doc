package com.ceres.cldoc;

import java.io.InputStream;
import java.util.List;

import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.ISession;

public interface ILayoutDefinitionService {
	void save(ISession session, LayoutDefinition ld);
	LayoutDefinition load(ISession session, String className, int typeId);
	List<LayoutDefinition> listLayoutDefinitions(ISession session, String filter, Integer typeId, Long entityType, Boolean isSingleton);
	void delete(ISession session, String className);
	
	String exportLayouts(ISession session);
	void importLayouts(ISession session, InputStream in);
}

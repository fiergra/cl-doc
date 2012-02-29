package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.LayoutDefinition;

public interface ILayoutDefinitionService {
	void save(Session session, LayoutDefinition ld);
	LayoutDefinition load(Session session, String className);
	List<LayoutDefinition> listLayoutDefinitions(Session session, String filter);
}

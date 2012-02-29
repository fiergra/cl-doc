package com.ceres.cldoc;

import java.util.Collection;

import com.ceres.cldoc.model.Catalog;

public interface ICatalogService {
	void save(Session session, Catalog catalog);
	
	Catalog load(Session session, long id);
	Collection<Catalog> loadList(Session session, String parentCode);
	Collection<Catalog> loadList(Session session, Catalog parent);
	
	void delete(Session session, Catalog catalog);
}

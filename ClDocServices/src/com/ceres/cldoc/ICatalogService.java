package com.ceres.cldoc;

import java.io.InputStream;
import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.Session;

public interface ICatalogService {
	void save(Session session, Catalog catalog);
	
	Catalog load(Session session, long id);
	Catalog load(Session session, String code);
	List<Catalog> loadList(Session session, String parentCode);
	List<Catalog> loadList(Session session, Catalog parent);
	
	void delete(Session session, Catalog catalog);
	
	String exportXML(Session session, Catalog parent);
	void importXML(Session session, InputStream inputStream);
}

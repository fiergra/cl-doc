package com.ceres.cldoc;

import java.io.InputStream;
import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.core.ISession;

public interface ICatalogService {
	void save(ISession session, Catalog catalog);
	
	Catalog load(ISession session, long id);
	Catalog load(ISession session, String code);
	List<Catalog> loadList(ISession session, String parentCode);
	List<Catalog> loadList(ISession session, Catalog parent);
	
	void delete(ISession session, Catalog catalog);
	
	String exportXML(ISession session, Catalog parent);
	void importXML(ISession session, InputStream inputStream);
}

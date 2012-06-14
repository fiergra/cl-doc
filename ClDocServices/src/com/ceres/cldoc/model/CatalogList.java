package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.List;

public class CatalogList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2576857674661726112L;

	public CatalogList(long listId) {
		this.id = listId;
	}
	
	public CatalogList() {
	}

	public Long id;
	public int type;
	public List<Catalog> list;
	
	public void addValue(Catalog catalog) {
		
	}
}

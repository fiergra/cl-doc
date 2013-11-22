package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CatalogList implements Serializable {

	private static final long serialVersionUID = 4707184842763254384L;
	
	public Long id;
	public int type;
	public List<Catalog> list;

	public CatalogList(long listId) {
		this.id = listId;
	}

	public CatalogList() {
	}

	public void addValue(Catalog value) {
		if (list == null) {
			list = new ArrayList<Catalog>();
		}
		list.add(value);
	}

	public void removeValue(Catalog value) {
		if (list != null) {
			Iterator<Catalog> iter = list.iterator();
			while (iter.hasNext()) {
				Catalog next = iter.next();
				
				if (next == value || next.equals(value) || (next.id != null && value.id != null && next.id.equals(value.id))) {
					iter.remove();
				}
			}
		}
		
	}

	public boolean isEmpty() {
		return list == null || list.isEmpty();
	}
	
}

package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EntityRelation implements Serializable, HasChildren<EntityRelation> {
	private static final long serialVersionUID = 6377906622183207175L;
	public Long id;
	public Entity subject;
	public Catalog type;
	public Entity object;
	public List<EntityRelation> children;
	public Date startDate;
	public Date endDate;
	
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	@Override
	public List<EntityRelation> getChildren() {
		return children;
	}
}

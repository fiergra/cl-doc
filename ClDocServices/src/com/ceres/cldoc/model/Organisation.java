package com.ceres.cldoc.model;

import com.ceres.core.IOrganisation;


public class Organisation extends Entity implements IOrganisation {

	private static final long serialVersionUID = -6144522368213126601L;

	public Organisation() {
		super(ENTITY_TYPE_ORGANISATION);
	}
	
}

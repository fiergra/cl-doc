package com.ceres.cldoc.shared.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Assignment implements Serializable {

	private static final long serialVersionUID = -6847677602213023116L;

	@Id
	public Long id;

	@Transient
	public Catalog catalog;
	public transient Key<Catalog> pkCatalog;
	
	@Transient
	public RealWorldEntity entity;
	public transient Key<RealWorldEntity> pkEntity;
	
	public Assignment() {
	}

}

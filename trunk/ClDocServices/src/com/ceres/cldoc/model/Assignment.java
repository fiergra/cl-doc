package com.ceres.cldoc.model;

import java.io.Serializable;

public class Assignment implements Serializable {

	private static final long serialVersionUID = -6847677602213023116L;
	public Long id;
	public Catalog catalog;
	public AbstractEntity entity;
	
	public Assignment() {
	}

}

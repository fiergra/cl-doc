package com.ceres.cldoc.model;

import java.io.Serializable;

public class ActClass implements Serializable {
	private static final long serialVersionUID = -2881390224214368359L;

	public static final ActClass EXTERNAL_DOC = new ActClass("externalDoc");

	public Long id;
	public String name;
	public String summaryDef;
	public Long entityType;
	public boolean isSingleton = false;

	
	
	public ActClass() {
		super();
	}



	public ActClass(Long id, String name, String summaryDef, Long entityType, boolean isSingleton) {
		this.id = id;
		this.name = name;
		this.summaryDef = summaryDef;
		this.entityType = entityType;
		this.isSingleton = isSingleton;
	}



	public ActClass(String name) {
		this(null, name, null, null, false);
	}



	public void initFrom(ActClass actClass) {
		this.id = actClass.id;
		this.name = actClass.name;
		this.entityType = actClass.entityType;
		this.isSingleton = actClass.isSingleton;
	}



	@Override
	public String toString() {
		return "#" + id + ":" + name;
	}



	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActClass) {
			return ((ActClass)obj).name.equals(name);
		} else {
			return super.equals(obj);
		}
	}
	
	
}

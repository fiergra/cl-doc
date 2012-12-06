package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public interface IAct extends Serializable {

	public abstract IActField get(String fieldName);

	public abstract String getString(String fieldName);

	public abstract Date getDate();
	public abstract void setDate(Date date);

	public abstract Date getDate(String fieldName);

	public abstract Long getLong(String fieldName);

	public abstract Float getFloat(String fieldName);

	public abstract boolean getBoolean(String fieldName);

	public abstract Catalog getCatalog(String fieldName);

	public abstract IActField set(String fieldName, Serializable value);

	public abstract CatalogList getCatalogList(String fieldName);
	
	public abstract Participation getParticipation(Catalog role);
	
	public abstract void setParticipant(Entity e, Catalog role);
}
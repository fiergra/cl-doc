package com.ceres.cldoc.model;

import java.util.Date;

public interface IGenericItem {

	public abstract IGenericItemField get(String fieldName);

	public abstract String getString(String fieldName);

	public abstract Date getDate(String fieldName);

	public abstract Long getLong(String fieldName);

	public abstract boolean getBoolean(String fieldName);

	public abstract Catalog getCatalog(String fieldName);

	public abstract <T> void set(String fieldName, T value);

}
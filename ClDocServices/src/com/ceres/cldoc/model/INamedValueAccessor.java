package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public interface INamedValueAccessor {

	public abstract INamedValueAccessor getValueBag(String fieldName);

	public abstract Object get(String fieldName);

	public abstract String getString(String fieldName);

	public abstract Date getDate(String fieldName);

	public abstract Long getLong(String fieldName);

	public abstract void set(String fieldName, Serializable value);

}
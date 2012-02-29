package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractNamedValueAccessor implements IGenericItem, Serializable {

	private static final long serialVersionUID = 285371339905648646L;

	@Override
	public String getString(String fieldName) {
		IGenericItemField field = get(fieldName);
		return field != null ? field.getStringValue() : null;
	}

	@Override
	public Long getLong(String fieldName) {
		IGenericItemField field = get(fieldName);
		return field != null ? field.getLongValue() : null;
	}

	@Override
	public Date getDate(String fieldName) {
		IGenericItemField field = get(fieldName);
		return field != null ? field.getDateValue() : null;
	}

	@Override
	public Catalog getCatalog(String fieldName) {
		IGenericItemField field = get(fieldName);
		return field != null ? field.getCatalogValue() : null;
	}

	@Override
	public boolean getBoolean(String fieldName) {
		Long l = getLong(fieldName);
		return l != null ? l.equals(1l) : false;
	}

	
}

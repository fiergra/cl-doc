package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractNamedValueAccessor implements IAct, Serializable {

	private static final long serialVersionUID = 285371339905648646L;

	@Override
	public CatalogList getCatalogList(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getListValue() : null;
	}

	@Override
	public String getString(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getStringValue() : null;
	}

	@Override
	public Long getLong(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getLongValue() : null;
	}

	@Override
	public Float getFloat(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getFloatValue() : null;
	}

	@Override
	public Date getDate(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getDateValue() : null;
	}

	@Override
	public Catalog getCatalog(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getCatalogValue() : null;
	}

	@Override
	public boolean getBoolean(String fieldName) {
		IActField field = get(fieldName);
		return field != null ? field.getBooleanValue() : Boolean.FALSE;
	}

	@Override
	public Participation getParticipation(Catalog role) {
		return null;
	}

	@Override
	public void setParticipant(Entity e, Catalog role) {
		
	}
	
	

	
}

package com.ceres.cldoc.shared.domain;

import java.util.Date;

public abstract class AbstractNamedValueAccessor implements INamedValueAccessor {

	@Override
	public INamedValueAccessor getValueBag(String fieldName) {
		return (INamedValueAccessor) get(fieldName);
	}

	@Override
	public String getString(String fieldName) {
		return (String) get(fieldName);
	}

	@Override
	public Long getLong(String fieldName) {
		return (Long) get(fieldName);
	}

	@Override
	public Date getDate(String fieldName) {
		return (Date) get(fieldName);
	}

}

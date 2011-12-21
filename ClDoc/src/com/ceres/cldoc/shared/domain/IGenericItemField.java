package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;

public interface IGenericItemField extends Serializable {
	String getName();
	Long getId();
	int getType();
	
//	T getValue();
//	void setValue(T value);
	
	void setValue(Object value);
	void setValue(String value);
	void setValue(Long value);
	void setValue(Date value);
	void setValue(Catalog value);
	
	Long getLongValue();
	String getStringValue();
	Date getDateValue();
	Catalog getCatalogValue();
}

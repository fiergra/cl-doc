package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public interface IActField extends Serializable {
	final int FT_STRING = 1;
	final int FT_INTEGER = 2;
	final int FT_DATE = 3;
	final int FT_REAL = 4;
	final int FT_BLOB = 5;
	final int FT_CATALOG = 6;
	final int FT_BOOLEAN = 7;

	String getName();
	Long getId();
	int getType();
	
//	T getValue();
//	void setValue(T value);
	
	void setValue(Serializable value);
//	void setValue(byte[] value);
//	void setValue(String value);
//	void setValue(Long value);
//	void setValue(Date value);
//	void setValue(Catalog value);
	
	Serializable getValue();
	byte[] getBlobValue();
	Long getLongValue();
	String getStringValue();
	Date getDateValue();
	Catalog getCatalogValue();
	void setId(long id);
	Boolean getBooleanValue();
}

package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class ActField implements IActField {

	private static final long serialVersionUID = 8653797810100446102L;
	private String name;
	private Long id;
	private int type;
	
//	private Catalog catalogValue;
//	private String stringValue;
//	private Date dateValue;
//	private Long longValue;
//	transient private byte[] blobValue;
	
	private Serializable value;
	
	public ActField(){}
	
	public ActField(Long id, String name, int type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public ActField(String name, Serializable value) {
		this(null, name, 0);
		this.value = value;
	}

//	public ActField(String name, Date value) {
//		this(null, name, 0);
//		this.dateValue = value;
//	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public int getType() {
		return type;
	}

//	@Override
//	public T getValue() {
//		return value;
//	}
//
//	@Override
//	public void setValue(String value) {
//		this.stringValue = value;
//	}
//
//	@Override
//	public void setValue(Long value) {
//		this.longValue = value;
//	}
//
//	@Override
//	public void setValue(Date value) {
//		this.dateValue = value;
//	}
//
	@Override
	public Boolean getBooleanValue() {
		return (Boolean)value;
	}

	@Override
	public Long getLongValue() {
		return (Long)value;
	}

	@Override
	public Float getFloatValue() {
		return (Float)value;
	}

	@Override
	public String getStringValue() {
		return (String)value;
	}

	@Override
	public Date getDateValue() {
		return (Date)value;
	}

//	@Override
//	public void setValue(Catalog value) {
//		this.catalogValue = value;
//	}
//
	@Override
	public Catalog getCatalogValue() {
		return (Catalog)value;
	}
	
	public void setValue(Object value) {
		if (value == null) {
			setValue((String)value);
		} else if (value instanceof String) {
			setValue((String)value);
			type = FT_STRING;
		} else if (value instanceof Long) {
			setValue((Long)value);
			type = FT_INTEGER;
		} else if (value instanceof Float) {
			setValue((Float)value);
			type = FT_FLOAT;
		} else if (value instanceof Boolean) {
			setValue((Boolean)value ? 1l : 0l);
			type = FT_BOOLEAN;
		} else if (value instanceof Date) {
			setValue((Date)value);
			type = FT_DATE;
		} else if (value instanceof Catalog) {
			setValue((Catalog)value);
			type = FT_CATALOG;
		} else {
			throw new RuntimeException("unsupported data type:" + (value != null ? value : "null"));
		}
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

//	@Override
//	public void setValue(byte[] value) {
//		blobValue = value;
//	}


	@Override
	public String toString() {
		return "#" + id + ": " + getValue();
	}

	public Serializable getValue() {
		return value;
//		switch (getType()) {
//		case FT_STRING: return stringValue;
//		case FT_CATALOG: return catalogValue;
//		case FT_BOOLEAN: return longValue;
//		case FT_DATE: return dateValue;
//		case FT_BLOB: return blobValue;
//		default: return null;
//		}
	}

	@Override
	public void setValue(Serializable value) {
		type = getType(value);
		this.value = value;
	}

	private int getType(Serializable value) {
		if (value == null) {
		} else if (value instanceof String) {
			return FT_STRING;
		} else if (value instanceof CatalogList) {
			return FT_LIST;
		} else if (value instanceof Long) {
			return FT_INTEGER;
		} else if (value instanceof Float) {
			return FT_FLOAT;
		} else if (value instanceof Boolean) {
			return FT_BOOLEAN;
		} else if (value instanceof Date) {
			return FT_DATE;
		} else if (value instanceof Catalog) {
			return FT_CATALOG;
		} else {
			throw new RuntimeException("unsupported data type:" + (value != null ? value : "null"));
		}
		return 0;
	}

	@Override
	public CatalogList getListValue() {
		return (CatalogList)value;
	}

	

}

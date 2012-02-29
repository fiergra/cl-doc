package com.ceres.cldoc.model;

import java.util.Date;

public class GenericItemField implements IGenericItemField {

	private static final long serialVersionUID = 8653797810100446102L;


	private String name;
	private Long id;
	private int type;
	
	private Catalog catalogValue;
	private String stringValue;
	private Date dateValue;
	private Long longValue;
	transient private byte[] blobValue;
	
	public GenericItemField(){}
	
	public GenericItemField(Long id, String name, int type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public GenericItemField(String name, String value) {
		this(null, name, 0);
		this.stringValue = value;
	}

	public GenericItemField(String name, Date value) {
		this(null, name, 0);
		this.dateValue = value;
	}

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
	@Override
	public void setValue(String value) {
		this.stringValue = value;
	}

	@Override
	public void setValue(Long value) {
		this.longValue = value;
	}

	@Override
	public void setValue(Date value) {
		this.dateValue = value;
	}

	@Override
	public Long getLongValue() {
		return longValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}

	@Override
	public Date getDateValue() {
		return dateValue;
	}

	@Override
	public void setValue(Catalog value) {
		this.catalogValue = value;
	}

	@Override
	public Catalog getCatalogValue() {
		return catalogValue;
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
		} else if (value instanceof Boolean) {
			setValue((Boolean)value ? 1l : 0l);
			type = FT_BOOLEAN;
		} else if (value instanceof Date) {
			setValue((Date)value);
			type = FT_DATE;
		} else if (value instanceof Catalog) {
			setValue((Catalog)value);
			type = FT_CATALOG;
		} else if (value instanceof byte[]) {
			setValue((byte[])value);
			type = FT_BLOB;
		} else {
			throw new RuntimeException("unsupported data type:" + (value != null ? value : "null"));
		}
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void setValue(byte[] value) {
		blobValue = value;
	}

	@Override
	public byte[] getBlobValue() {
		return blobValue;
	}

	

}

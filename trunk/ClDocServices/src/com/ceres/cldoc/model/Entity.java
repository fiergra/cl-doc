package com.ceres.cldoc.model;

import java.util.ArrayList;
import java.util.List;

import com.ceres.core.IEntity;

public class Entity implements IEntity {
	private static final long serialVersionUID = 1L;

	public static final int ENTITY_TYPE_PERSON = 181;
	public static final int ENTITY_TYPE_ORGANISATION = 182;
	public static final int ENTITY_TYPE_ROOM = 183;

	public static final String DISPLAY_NAME = "name";

	protected Long id;

	protected String name;
	@Override
	public String getName() {
		return name;
	}

	private int type;
	
	public List<Address> addresses;
	
	public Entity() {
	}
	
	public Entity(int type) {
		this.type = type;
	}

	public Entity(long id, int type, String name) {
		this(type);
		this.id = id;
		this.name = name;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		return (arg0 instanceof Entity) ? hashCode() == arg0.hashCode() : super.equals(arg0);
	}

	public void addAddress(Address address) {
		if (addresses == null) {
			addresses = new ArrayList<Address>();
		}
		address.entity = this;
		addresses.add(address);
	}
	
	public Address getPrimaryAddress() {
		Address primaryAddress = null;
		
		if (addresses == null) {
			primaryAddress = new Address();
			addAddress(primaryAddress);
		} else {
			primaryAddress = addresses.get(0);
		}
		
		return primaryAddress;
	}

	@Override
	public String toString() {
		return id + "|" + name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDisplayId() {
		return id;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
}

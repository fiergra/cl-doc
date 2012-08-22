package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int ENTITY_TYPE_PERSON = 1001;
	public static final int ENTITY_TYPE_ORGANISATION = 1002;
	public static final int ENTITY_TYPE_ROOM = 1003;

	public Long id;

	protected String name;
	public String getName() {
		return name;
	}

	public int type;
	
	public List<Address> addresses;
	
	public Entity() {
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
	
	
	
}

package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int ENTITY_TYPE_PERSON = 1;
	public static final int ENTITY_TYPE_ORGANISATION = 39;
	public static final int ENTITY_TYPE_ROOM = 40;

	public Long id;

	public String name;

	public int type;
	
	public List<Address> addresses;
	
	public AbstractEntity() {
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		return (arg0 instanceof AbstractEntity) ? hashCode() == arg0.hashCode() : super.equals(arg0);
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
	
	
}

package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public Long id;

	public String name;

	public int type;
	
	public Collection<Address> addresses;
	
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
	
}

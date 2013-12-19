package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.HashMap;

import com.ceres.cldoc.model.Address;
import com.ceres.cldoc.model.Entity;

public class AddressWrapper extends HashMap<String, Serializable> {
	private final Address address;

	public AddressWrapper(Address address) {
		this.address = address;
		put("city", address.city);
		put("co", address.co);
		put("entity", address.entity);
		put("id", address.id);
		put("note", address.note);
		put("number", address.number);
		put("phone", address.phone);
		put("postCode", address.postCode);
		put("street", address.street);
	}
	
	public Address unwrap() {
		address.city = (String)get("city");
		address.co = (String)get("co");
		address.entity = (Entity)get("entity");
		address.id = (Long)get("id");
		address.note= (String)get("note");
		address.number = (String)get("number");
		address.phone = (String)get("phone");
		address.postCode = (String)get("postCode");
		address.street = (String)get("street");

		return address;
	}
	
}

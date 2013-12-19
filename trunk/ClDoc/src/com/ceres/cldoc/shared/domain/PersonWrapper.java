package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Person;

public class PersonWrapper extends HashMap<String, Serializable> {

	private static final long serialVersionUID = 184132632098539674L;
	private final Person person;

	public PersonWrapper(Person person) {
		this.person = person;
		put("id", person.getId());
		put("firstName", person.firstName);
		put("lastName", person.lastName);
		put("gender", person.gender);
		put("maidenName", person.maidenName);
		put("dateOfBirth", person.dateOfBirth);
		put("primaryAddress", new AddressWrapper(person.getPrimaryAddress()));
	}

	public Person unwrap() {
		AddressWrapper aw = (AddressWrapper) get("primaryAddress");
		person.setPrimaryAddress(aw != null ? aw.unwrap() : null);
		person.firstName = (String) get("firstName");
		person.lastName = (String) get("lastName");
		person.gender = (Catalog) get("gender");
		person.maidenName = (String) get("maidenName");
		person.dateOfBirth = (Date) get("dateOfBirth");
		return person;
	}
}

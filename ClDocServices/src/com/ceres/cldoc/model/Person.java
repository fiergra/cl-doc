package com.ceres.cldoc.model;

import java.util.Date;

public class Person extends Entity {

	private static final long serialVersionUID = -6144522368213126601L;

	public String firstName;
	public String lastName;
	public String maidenName;
	public Date dateOfBirth;
	public Catalog gender;

	public Person() {
		super(ENTITY_TYPE_PERSON);
	}

	public Person(Long id, String firstName, String lastName) {
		this();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String getName() {
		return firstName + " " + lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	
}

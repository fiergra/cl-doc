package com.ceres.cldoc.model;

import java.util.Date;

import com.ceres.core.IPerson;

public class Person extends Entity implements IPerson {

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

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	
}

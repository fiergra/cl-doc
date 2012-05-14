package com.ceres.cldoc.model;

import java.util.Date;

public class Person extends AbstractEntity {

	private static final long serialVersionUID = -6144522368213126601L;

	public long perId;
	public String firstName;
	public String lastName;
	public String maidenName;
	public Date dateOfBirth;
	public Catalog gender;

	public Person() {
		super();
		type = ENTITY_TYPE_PERSON;
	}
	
}

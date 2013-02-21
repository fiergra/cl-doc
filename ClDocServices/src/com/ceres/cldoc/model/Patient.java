package com.ceres.cldoc.model;


public class Patient extends Person {

	private static final long serialVersionUID = -6144522368213126601L;

	public long perId;

	public Patient() {
		super();
	}
	
	public Patient(Person person) {
		this.addresses = person.addresses;
		this.dateOfBirth = person.dateOfBirth;
		this.firstName = person.firstName;
		this.gender = person.gender;
		this.id = person.id;
		this.lastName = person.lastName;
		this.maidenName = person.maidenName;
		this.type = person.type;
	}

	@Override
	public Long getDisplayId() {
		return perId;
	}

}

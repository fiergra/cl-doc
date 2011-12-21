package com.ceres.cldoc.shared.domain;

import javax.persistence.Embedded;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class Person extends RealWorldEntity {
	private static final long serialVersionUID = -4334356676536523657L;

	@Embedded
	public Address primaryAddress;

	@Embedded
	public Address secondaryAddress;
	
	public Person() {
		primaryAddress = new Address();
		secondaryAddress = new Address();
	}
}

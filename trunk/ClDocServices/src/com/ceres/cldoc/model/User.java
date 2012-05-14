package com.ceres.cldoc.model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 4918516919583093321L;

	public Long id;
	public String userName;
	public String hash;
	public Person person;
	public Organisation organisation;

	public User() {
		super();
	}

}
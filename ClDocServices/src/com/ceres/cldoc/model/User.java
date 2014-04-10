package com.ceres.cldoc.model;

import java.util.Set;

import com.ceres.cldoc.model.IOrganisation;
import com.ceres.cldoc.model.IPerson;
import com.ceres.cldoc.model.IUser;

public class User implements IUser {
	private static final long serialVersionUID = 4918516919583093321L;

	public Long id;
	public String userName;
	public String hash;
	public Person person;
	public Organisation organisation;
	public Set<Catalog> roles;
	
	public User() {
		super();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public IOrganisation getOrganisation() {
		return organisation;
	}

	@Override
	public IPerson getPerson() {
		return person;
	}

	@Override
	public String getUserName() {
		return userName;
	}

}

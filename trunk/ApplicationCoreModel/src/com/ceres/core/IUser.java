package com.ceres.core;

import java.io.Serializable;

public interface IUser extends Serializable {

	long getId();

	IOrganisation getOrganisation();

	IPerson getPerson();

	String getUserName();

}

package com.ceres.core;

import java.io.Serializable;

public interface ISession extends Serializable  {

	long getId();

	IUser getUser();

	boolean isAllowed(IAction action);

}

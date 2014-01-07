package com.ceres.core;

import java.io.Serializable;

public interface IEntity extends Serializable {

	Long getId();

	String getName();
	int getType();

}

package com.ceres.core;

import java.io.Serializable;

public interface IEntity extends Serializable {

	Long getId();

	int getType();

	String getName();

}

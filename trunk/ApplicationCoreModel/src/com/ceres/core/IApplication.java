package com.ceres.core;


public interface IApplication {

	String getLabel(String label);

	void status(String name);

	ISession getSession();

}

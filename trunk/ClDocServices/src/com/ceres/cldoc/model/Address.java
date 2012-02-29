package com.ceres.cldoc.model;

import java.io.Serializable;

public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	public Long id;
	public AbstractEntity entity;
	public String street;
	public String number;
	public String co;
	public String city;
	public String postCode;
	
}

package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	public String street;
	public String number;
	public String co;
	public String city;
	public String postCode;
	
}

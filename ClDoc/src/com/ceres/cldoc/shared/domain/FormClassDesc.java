package com.ceres.cldoc.shared.domain;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class FormClassDesc implements Serializable {

	private static final long serialVersionUID = 7581049677245652840L;

	@Id
	public String name;

	public String xmlLayout;
	
}

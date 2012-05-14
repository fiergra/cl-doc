package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class Participation implements Serializable {

	private static final long serialVersionUID = 8017405646839706167L;

	public static final long PATIENT = 101l;

	public Long id;
	public AbstractEntity entity;
	public Catalog role;
	public Act act;
	public Date start;
	public Date end;

	public Participation() {
	}

	public Participation(Act act, AbstractEntity entity, Catalog role, Date start, Date end) {
		this.entity = entity;
		this.role = role;
		this.act = act;
		this.start = start;
		this.end = end;
	}

}

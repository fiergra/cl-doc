package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class Participation implements Serializable {

	private static final long serialVersionUID = 8017405646839706167L;

	public static final Catalog PROTAGONIST = new Catalog(101l, "PROTAGONIST");
	public static final Catalog ORGANISATION = new Catalog(102l, "ORGANISATION");
	public static final Catalog ADMINISTRATOR = new Catalog(103l, "ADMINISTRATOR");

	public Long id;
	public Entity entity;
	public Catalog role;
	public Act act;
	public Date start;
	public Date end;

	public Participation() {
	}

	public Participation(Act act, Entity entity, Catalog role, Date start, Date end) {
		this.entity = entity;
		this.role = role;
		this.act = act;
		this.start = start;
		this.end = end;
	}

}

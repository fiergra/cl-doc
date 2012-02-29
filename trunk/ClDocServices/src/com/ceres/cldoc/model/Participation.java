package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class Participation implements Serializable {

	private static final long serialVersionUID = 8017405646839706167L;

	public Long id;
	public AbstractEntity entity;
	public GenericItem item;
	public Date start;
	public Date end;

	public Participation() {
	}

	public Participation(GenericItem item, AbstractEntity entity, Date start, Date end) {
		this.entity = entity;
		this.item = item;
		this.start = start;
		this.end = end;
	}

}

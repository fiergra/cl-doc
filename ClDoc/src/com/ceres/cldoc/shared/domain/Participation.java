package com.ceres.cldoc.shared.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Participation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public transient Key<ValueBag> pkValueBag;
	public transient Key<RealWorldEntity> pkEntity;
	@Transient
	public RealWorldEntity entity;
	@Transient
	public ValueBag valueBag;

	public Participation() {
	}

	public Participation(ValueBag valueBag, RealWorldEntity entity) {
		this.entity = entity;
		this.valueBag = valueBag;
	}

}

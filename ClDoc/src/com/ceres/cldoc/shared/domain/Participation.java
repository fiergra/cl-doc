package com.ceres.cldoc.shared.domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Participation implements Serializable {

	private static final long serialVersionUID = 8017405646839706167L;

	@Id
	public Long id;

	public transient Key<GenericItem> pkValueBag;
	public transient Key<RealWorldEntity> pkEntity;
	@Transient
	public RealWorldEntity entity;
	@Transient
	public INamedValueAccessor valueBag;

	public Participation() {
	}

	public Participation(INamedValueAccessor valueBag, RealWorldEntity entity) {
		this.entity = entity;
		this.valueBag = valueBag;
	}

}

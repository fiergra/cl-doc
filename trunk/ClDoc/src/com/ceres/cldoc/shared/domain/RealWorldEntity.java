package com.ceres.cldoc.shared.domain;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class RealWorldEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	public RealWorldEntity() {
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		return (arg0 instanceof RealWorldEntity) ? hashCode() == arg0.hashCode() : super.equals(arg0);
	}

	
	
}

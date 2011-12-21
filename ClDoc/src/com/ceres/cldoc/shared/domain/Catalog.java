package com.ceres.cldoc.shared.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Catalog implements Serializable {

	public final static String ROOT = "ROOT";
	
	private static final long serialVersionUID = -6847677602213023115L;

	@Id
	public String code;
	public String text;
	public String shortText;
	public Date date;

	@Parent
	public Key<Catalog> parent;
	@Transient
	public Catalog parentCatalog;
	@Transient
	public List<Catalog> children;
	
	public Catalog() {
	}

	@Override
	public String toString() {
		return code;
	}

	public void addChild(Catalog child) {
		if (children == null) {
			children = new ArrayList<Catalog>();
		}
		children.add(child);
	}

	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}
	

}

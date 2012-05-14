package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Catalog implements Serializable {

	public final static String ROOT = "ROOT";
	public static final Catalog ADMIN = new Catalog(10l);
	public static final Catalog USER = new Catalog(11l);
	public static final Catalog GUEST = new Catalog(12l);
	
	public static final Catalog PATIENT = new Catalog(Participation.PATIENT);
	
	private static final long serialVersionUID = -6847677602213023115L;


	public Long id;
	public String code;
	public String text;
	public String shortText;
	public Date date;

	public Catalog parent;
	public Collection<Catalog> children;
	
	public Catalog() {
	}

	public Catalog(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return code;
	}
	

	public void addChild(Catalog child) {
		if (children == null) {
			children = new ArrayList<Catalog>();
		}
		child.parent = this;
		children.add(child);
	}

	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}
	

}

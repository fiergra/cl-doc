package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Catalog implements Serializable, HasChildren<Catalog> {

	public final static String ROOT = "ROOT";
	public static final Catalog ADMIN = new Catalog(10l);
	public static final Catalog USER = new Catalog(11l);
	public static final Catalog GUEST = new Catalog(12l);
	
	public static final Catalog PATIENT = new Catalog(Participation.PATIENT);
	public static final Catalog ORGANISATION = new Catalog(Participation.ORGANISATION);
	public static final Catalog MASTERDATA = new Catalog(Participation.MASTERDATA);

	public static final Catalog VIEW = new Catalog(71l, "VIEW");
	public static final Catalog EDIT = new Catalog(72l, "EDIT");

	private static final long serialVersionUID = -6847677602213023115L;
	

	public Long id;
	public String code;
	public String text;
	public String shortText;
	public Date date;

	public Catalog parent;
	public List<Catalog> children;
	public Long number1;
	public Long number2;
	
	public Catalog() {
	}

	public Catalog(long id) {
		this.id = id;
	}

	public Catalog(long id, String code) {
		this(id);
		this.code = code;
	}

	@Override
	public String toString() {
		return id != null ? id + "|" + code : code;
	}
	

	public void addChild(Catalog child) {
		if (children == null) {
			children = new ArrayList<Catalog>();
		}
		child.parent = this;
		children.add(child);
	}

	@Override
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	public void removeChild(Catalog child) {
		if (hasChildren()) {
			Iterator<Catalog> iter = children.iterator();
			while (iter.hasNext()) {
				Catalog next = iter.next();
				
				if (next == child || next.equals(child) || (next.id != null && child.id != null && next.id.equals(child.id))) {
					iter.remove();
				}
			}
		}
		
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Catalog) {
			Catalog c = (Catalog)arg0;
			if (c.id != null && this.id != null) {
				return c.id.equals(this.id);
			}
		}
		
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public List<Catalog> getChildren() {
		return children;
	}
	

}

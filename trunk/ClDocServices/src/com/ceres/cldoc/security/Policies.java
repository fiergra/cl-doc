package com.ceres.cldoc.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.User;

public class Policies implements Serializable {
	private static final long serialVersionUID = -9153560658105493230L;
	private HashMap<Catalog,Collection<Catalog>> actions;
	
	public Policies() {
	}
	
	public Policies(User user, HashMap<Catalog, Collection<Catalog>> actions) {
		super();
		this.actions = actions;
	}
	
	public boolean isAllowed(Catalog type, Catalog action) {
		Collection<Catalog> allowed = actions.get(type);
		return contains(allowed, action);
	}

	private boolean contains(Collection<Catalog> allowed, Catalog action) {
		if (allowed != null) {
			Iterator<Catalog> iter = allowed.iterator();
			while (iter.hasNext()) {
				if (iter.next().id.equals(action.id)) {
					return true;
				}
			}
		}
		return false;
	}
	
}

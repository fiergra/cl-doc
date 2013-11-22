package com.ceres.cldoc.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.ceres.cldoc.model.User;

public class Policies implements Serializable {
	private static final long serialVersionUID = -9153560658105493230L;
	private HashMap<String,Collection<String>> actions;
	
	public Policies() {
	}
	
	public Policies(User user, HashMap<String, Collection<String>> actions) {
		super();
		this.actions = actions;
	}
	
	public boolean isAllowed(String type, String action) {
		Collection<String> allowed = actions.get(type);
		return contains(allowed, action);
	}

	private boolean contains(Collection<String> allowed, String action) {
		if (allowed != null) {
			Iterator<String> iter = allowed.iterator();
			while (iter.hasNext()) {
				if (iter.next().equals(action)) {
					return true;
				}
			}
		}
		return false;
	}
	
}

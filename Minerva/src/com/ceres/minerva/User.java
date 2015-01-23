package com.ceres.minerva;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class User implements Serializable {
	private static final long serialVersionUID = -5342534826597898787L;
	
	public String name;
	public Collection <Role> roles;
	
	public User() {} 
	
	public User(String name) {
		this.name = name;
	} 
	
	public void assignRole(Role role) {
		if (roles == null) {
			roles = new HashSet<>();
		}
		roles.add(role);
		if (role.hasChildren()) {
			for (Role r:role.children) {
				assignRole(r);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return obj.equals(name);
		} else if (obj instanceof User) {
			return ((User)obj).name.equals(name);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	
}

package com.ceres.minerva;

import java.util.Collection;
import java.util.HashSet;


public class Role extends User {
	private static final long serialVersionUID = -1709533367320996794L;

	public Role parent;
	public Collection<Role> children;
	
	public Role(String name) {
		super(name);
	}

	public void addChild(Role child) {
		if (children == null) {
			children = new HashSet<>();
		}
		children.add(child);
		child.parent = this;
	}

	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}
}

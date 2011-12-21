package com.ceres.cldoc.shared.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LayoutElement implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<LayoutElement> children = new ArrayList<LayoutElement>();

	private String type;
	private HashMap <String, String> attributes = new HashMap<String, String>();
	private LayoutElement parent;
	
	public LayoutElement() {}

	public LayoutElement(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public String setAttribute(String name, String value) {
		return attributes.put(name, value);
	}
	
	public void addChild(LayoutElement child) {
		children.add(child);
		child.parent = this;
	}
	

	public ArrayList<LayoutElement> getChildren() {
		return children;
	}

	public LayoutElement getParent() {
		return parent;
	}

	public HashMap<String, String> getAttributes() {
		return attributes;
	}

}

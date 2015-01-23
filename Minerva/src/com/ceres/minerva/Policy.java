package com.ceres.minerva;

import java.io.Serializable;

public class Policy implements Serializable {
	private static final long serialVersionUID = -5342534826597898788L;
	
	public User user;
	public String type; 
	public String state; 
	public String action; 
	public String scope;
	
	public Policy() {}
	
	public Policy(User user, String type, String state, String action,String scope) {
		super();
		this.user = user;
		this.type = type;
		this.state = state;
		this.action = action;
		this.scope = scope;
	} 
	
	
}

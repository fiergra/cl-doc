package com.ceres.cldoc;

import com.ceres.cldoc.model.IAction;


public class Action implements IAction {
	public final String type;
	public final String action;
	
	public Action(String type, String action) {
		super();
		this.type = type;
		this.action = action;
	}
	
	
}

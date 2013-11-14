package com.ceres.cldoc;

import com.ceres.cldoc.model.Catalog;
import com.ceres.core.IAction;

public class Action implements IAction {
	public final Catalog type;
	public final Catalog action;
	
	public Action(Catalog type, Catalog action) {
		super();
		this.type = type;
		this.action = action;
	}
	
	
}

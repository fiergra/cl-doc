package com.ceres.cldoc.timemanagement;

import com.ceres.cldoc.model.Entity;

public class WorkPattern extends Entity {
	private static final long serialVersionUID = -4403661379320157162L;

	public WorkPattern() {
		super(1001);
	}

	public float[] hours;
	public String pattern;
	
}

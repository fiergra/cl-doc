package com.ceres.cldoc.timemanagement;

import com.ceres.cldoc.model.Entity;

public class WorkPattern extends Entity {

	public WorkPattern() {}

	public WorkPattern(Entity e) {
		super(e.getId(), e.getType(), e.getName());
	}

	private static final long serialVersionUID = -4403661379320157162L;

	public float weeklyHours;
}

package com.ceres.cldoc.client.views;

public interface ValidationStatus {
	public enum States {none, required, valid};
	
	void set(States state);
}

package com.ceres.minerva;

import java.io.Serializable;

public class ScopedAction implements Serializable {
	private static final long serialVersionUID = -1326114921375699913L;
	public String action;
	public String scope;

	public ScopedAction() {}
	
	public ScopedAction(String action, String scope) {
		super();
		this.action = action;
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		return action + "{" + (scope != null ? scope : "") + "}";
	}

	@Override
	public int hashCode() {
		return action.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScopedAction) {
			return action.equals(((ScopedAction)obj).action);
		} else if (obj instanceof String) {
			return action.equals((String)obj);
		} else {
			return super.equals(obj);
		}
	}
	

}

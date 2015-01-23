package com.ceres.minerva;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Minerva implements Serializable {

	private static final long serialVersionUID = -6635615234803744166L;

	private HashMap<String, HashMap<String, Collection<Policy>>> policiesByType = new HashMap<>();
	
	public void addPolicy(User user, String type, String state, String action, String scope) {
		Policy policy = new Policy(user, type, state, action, scope);
		addPolicy(policy);
	}

	public void addPolicy(Policy policy) {
		HashMap<String, Collection<Policy>> byType = policiesByType.get(policy.type);
		if (byType == null) {
			byType = new HashMap<>();
			policiesByType.put(policy.type, byType);
		}
		
		Collection<Policy> byState = byType.get(policy.state);
		if (byState == null) {
			byState = new ArrayList<>();
			byType.put(policy.state, byState);
		}
		byState.add(policy);
	}
	
	public boolean isActionAllowed(User user, String type, String state, String action) {
		return false;
	}
}

package com.ceres.cldoc;

import java.io.Serializable;

import com.ceres.cldoc.model.IAction;
import com.ceres.cldoc.model.User;
import com.ceres.cldoc.security.Policies;

public class Session implements Serializable {

	private static final long serialVersionUID = -5371736346089900693L;

	private User user;
	private long id;
	private Policies policies;

	private String dbUserName;

	private String dbURL;
	
	private static long sessionIds = 1;
	
	public Session() {
	}

	public boolean isAllowed(IAction action) {
		Action a = (Action)action;
		return policies != null ? policies.isAllowed(a.type, a.action) : false;
	}

	//	public Session(User user, long id) {
//		this.user = user;
//		this.id = id;
//	}
//
	public Session(User user, String dbUserName, String dbURL, Policies policies) {
		this.user = user;
		this.dbUserName = dbUserName;
		this.dbURL = dbURL;
		this.policies = policies;
		this.id = createSessionId();
	}

	private synchronized static long createSessionId() {
		return sessionIds++;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}
	
	

	
}

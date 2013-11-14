package com.ceres.cldoc;

import java.io.Serializable;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.User;
import com.ceres.cldoc.security.Policies;
import com.ceres.core.IAction;
import com.ceres.core.ISession;
import com.ceres.core.IUser;

public class Session implements ISession, Serializable {

	private static final long serialVersionUID = -5371736346089900693L;

	private User user;
	private long id;
	private Policies policies;
	
	private static long sessionIds = 1;
	
	public Session() {
	}

	@Override
	public boolean isAllowed(IAction action) {
		Action a = (Action)action;
		return policies != null ? policies.isAllowed(a.type, a.action) : false;
	}

	private boolean isAllowed(Catalog type, Catalog action) {
		return policies != null ? policies.isAllowed(type, action) : false;
	}
//	public ISession(User user, long id) {
//		this.user = user;
//		this.id = id;
//	}
//
	public Session(User user, Policies policies) {
//		this(user, createISessionId());
		this.user = user;
		this.policies = policies;
		this.id = createISessionId();
	}

	private synchronized static long createISessionId() {
		return sessionIds++;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public IUser getUser() {
		return user;
	}
	
	

	
}

package com.ceres.cldoc;

import java.io.Serializable;

import com.ceres.cldoc.model.User;

public class Session implements Serializable {

	private static final long serialVersionUID = -5371736346089900693L;

	private User user;
	private long id;
	
	private static long sessionIds = 1;
	
	public Session() {
	}

	public Session(User user) {
		this.user = user;
		this.id = createSessionId();
	}

	private synchronized long createSessionId() {
		return sessionIds++;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}
	
	

	
}

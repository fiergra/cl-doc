package com.ceres.cldoc;

import junit.framework.TestCase;

import com.ceres.cldoc.model.User;

public class TransactionalTest extends TestCase {

	private User user;
	private Session session;
	
	public User getUser() {
		return user;
	}

	public Session getSession() {
		return session;
	}

	protected void setUp() throws Exception {
		super.setUp();
		user = new User();
		user.id = 1l;
		session = new Session(user);
		TxManager.start(session);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		TxManager.cancel(session);
	}

}

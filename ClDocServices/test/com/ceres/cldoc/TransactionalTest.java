package com.ceres.cldoc;

import junit.framework.TestCase;

import com.ceres.cldoc.model.User;
import com.ceres.core.ISession;

public class TransactionalTest extends TestCase {

	private User user;
	private ISession session;
	
	public User getUser() {
		return user;
	}

	public ISession getSession() {
		return session;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		user = new User();
		user.id = 1l;
		session = new Session(user, null);
		TxManager.start(session);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TxManager.cancel(session);
	}

}

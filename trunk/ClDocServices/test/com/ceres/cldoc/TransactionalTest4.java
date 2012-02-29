package com.ceres.cldoc;

import org.junit.After;
import org.junit.Before;

import com.ceres.cldoc.model.User;

public class TransactionalTest4 {

	private User user;
	private Session session;

	
	
	public User getUser() {
		return user;
	}

	public Session getSession() {
		return session;
	}

	@Before
	public void setUp() {
		user = new User();
		session = new Session(user);
		TxManager.start(session);
	}
	
	@After
	public void tearDown() {
		TxManager.cancel(session);
	}
	
}

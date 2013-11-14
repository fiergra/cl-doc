package com.ceres.cldoc;

import junit.framework.TestCase;

import com.ceres.core.ISession;

public class TxManagerTest extends TestCase {
	
	public void testNestedTx() {
		ISession session = new Session();
		
		TxManager.start(session);
		TxManager.start(session);
		TxManager.end(session);
		TxManager.end(session);
	}

}

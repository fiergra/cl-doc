package com.ceres.cldoc;

import junit.framework.TestCase;

public class TxManagerTest extends TestCase {
	
	public void testNestedTx() {
		Session session = new Session();
		
		TxManager.start(session);
		TxManager.start(session);
		TxManager.end(session);
		TxManager.end(session);
	}

}

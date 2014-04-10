package com.ceres.cldoc;

import java.sql.SQLException;

import junit.framework.TestCase;

import com.ceres.cldoc.model.ISession;

public class TxManagerTest extends TestCase {
	
	public void testNestedTx() throws SQLException {
		ISession session = new Session();
		
		TxManager.start(session);
		TxManager.start(session);
		TxManager.end(session);
		TxManager.end(session);
	}

}

package com.ceres.minerva;

import junit.framework.TestCase;

public class TestMinerva extends TestCase {

	public void testMinerva() throws Exception {
		User u = new User("fiergra");
		Role r1 = new Role("r1");
		Role r2 = new Role("r2");
		
		r1.addChild(r2);
		u.assignRole(r1);
		
		assertEquals(2, u.roles.size());
	}
}

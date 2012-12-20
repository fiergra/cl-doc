package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;

public class UserServiceTest extends TransactionalTest {

	public void testAll() {
		Person person = new Person();
		person.firstName = "Heinz";
		person.lastName = "Achmed";
		Locator.getEntityService().save(getSession(), person);
		Entity orga = new Entity();
		orga.type = Entity.ENTITY_TYPE_ORGANISATION;
		orga.setName("junit");
		Locator.getEntityService().save(getSession(), orga);
		IUserService us = Locator.getUserService();
		User testUser = us.register(getSession(), person, orga, "hachmed", "kibitzer");
		
		List<User> users = Locator.getUserService().listUsers(getSession(), "hach");
		
		assertNotNull(users);
		assertFalse(users.isEmpty());

		assertTrue(testUser.roles == null || testUser.roles.isEmpty());
		List<Catalog> roles = Locator.getCatalogService().loadList(getSession(), "ROLES");
		Catalog role = roles.iterator().next();
		us.addRole(getSession(), testUser, role);
		
		testUser = us.listUsers(getSession(), "hachmed").get(0);
		assertFalse(testUser.roles.isEmpty());
		
		us.removeRole(getSession(), testUser, role);
		testUser = us.listUsers(getSession(), "hachmed").get(0);
		assertTrue(testUser.roles.isEmpty());
	}
}

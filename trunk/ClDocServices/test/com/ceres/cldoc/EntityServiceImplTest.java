package com.ceres.cldoc;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ceres.cldoc.model.Address;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;

public class EntityServiceImplTest extends TransactionalTest4 {

	@Test
	public void testRelations() {
		IEntityService entityService = Locator.getEntityService();
		List<Organisation> organisations = entityService.list(getSession(), Entity.ENTITY_TYPE_ORGANISATION);
		List<Person> persons = entityService.list(getSession(), Entity.ENTITY_TYPE_PERSON);
		
		List<EntityRelation> relations = entityService.listRelations(getSession(), organisations.get(0), true);
	}
	
	@Test
	public void testSelectByType() {
		IEntityService entityService = Locator.getEntityService();
		List<Organisation> organisations = entityService.list(getSession(), Entity.ENTITY_TYPE_ORGANISATION);
		List<Person> persons = entityService.list(getSession(), Entity.ENTITY_TYPE_PERSON);
		
	}
	
	@Test
	public void testCatalog() {
		ICatalogService catalogService = Locator.getCatalogService();
		Catalog catalog1 = createCatalog(null, "code1", "text1", "short1");
		Catalog catalog2 = createCatalog(catalog1, "code2", "text2", "short2");
		Catalog catalog3 = createCatalog(catalog1, "code3", "text3", "short3");
		Catalog catalog4 = createCatalog(catalog1, "code4", "text4", "short4");
		
		catalogService.save(getSession(), catalog1);
		catalogService.save(getSession(), catalog2);
		catalogService.save(getSession(), catalog3);
		catalogService.save(getSession(), catalog4);
		
		Collection<Catalog> catalogs = catalogService.loadList(getSession(), catalog1);
		Assert.assertEquals(3, catalogs.size());
	}

	private Catalog createCatalog(Catalog parent, String code, String text, String shortText) {
		Catalog catalog = new Catalog();
		catalog.code = code;
		catalog.parent = parent;
		catalog.text = text;
		catalog.shortText = shortText;
		catalog.date = new Date();
		return catalog;
	}
	
	@Test
	public void testLogin() {
		IUserService userService = Locator.getUserService();
		IEntityService entityService = Locator.getEntityService();
		
		Session session = userService.login(getSession(), "heinz", "achmed");
		Assert.assertNull(session);
		Person person = new Person();
		person.firstName = "Heinz";
		person.lastName = "Achmed";
		Organisation o = entityService.load(getSession(), 20);
		userService.register(getSession(), person, o, "heinz", "achmed");
		session = userService.login(getSession(), "heinz", "achmed");
		Assert.assertNotNull(session);
	}
	
	@Test
	public void testSave() {
		Person person = new Person();
		person.name = "Heinz";
		
		Address a = new Address();
		a.street = "Strassenname";
		a.number = "10b";
		a.co = "dritte Stiege";
		a.postCode = "A1015";
		a.city = "Wien";
		
		person.addAddress(a);
		IEntityService entityService = Locator.getEntityService();
		
//		TxManager.cancel(getSession());
		
		entityService.save(getSession(), person);
		long id = person.id;
		person = entityService.load(getSession(), id);
		
		Assert.assertNotNull(person.addresses);
		Assert.assertFalse(person.addresses.isEmpty());
	}

}

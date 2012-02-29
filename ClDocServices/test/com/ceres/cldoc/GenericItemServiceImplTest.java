package com.ceres.cldoc;

import java.util.Collection;
import java.util.Date;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.Person;

public class GenericItemServiceImplTest extends TransactionalTest {

	public void testSubCatalog() {
		ICatalogService catalogService = Locator.getCatalogService();
		Collection<Catalog> catalogs = catalogService.loadList(getSession(), "CLDOC.MAIN");
		assertNotNull(catalogs);
		assertTrue(!catalogs.isEmpty());
	}
	
	public void testSave() {
		IGenericItemService genericItemService = Locator.getGenericItemService();
		
		GenericItem item = new GenericItem("Unknown");
		
		genericItemService.save(getSession(), item);
		Date d = new Date(0l);
		item.date = d;
		genericItemService.save(getSession(), item);
		assertEquals(d, item.date);
		
		ICatalogService catalogService = Locator.getCatalogService();

		Catalog c0 = new Catalog();
		c0.code = "Versandhaus";
		c0.shortText = "vsh";
		c0.text = "vsh";
		catalogService.save(getSession(), c0);
		
		Catalog c1 = new Catalog();
		c1.parent = c0;
		c1.code = "Quelle";
		c1.shortText = "kurz";
		c1.text = "lang";
		catalogService.save(getSession(), c1);
		
		Catalog c2 = new Catalog();
		c2.parent = c0;
		c2.code = "Neckermann";
		c2.shortText = "kurz2";
		c2.text = "lang2";
		catalogService.save(getSession(), c2);
		
		item.set("string", "asdf");
		item.set("long", 1l);
		item.set("date", new Date());
		item.set("catalog", c1);
	
		genericItemService.save(getSession(), item);
		
		item.set("string", "asdf2");
		item.set("long", 2l);
		item.set("date", new Date());
		item.set("catalog", c2);
	
		genericItemService.save(getSession(), item);
		
		item = genericItemService.load(getSession(), item.id);
		assertEquals("asdf2", item.getString("string"));
		assertEquals(new Long(2l), item.getLong("long"));
		assertEquals(c2.code, item.getCatalog("catalog").code);
		
		IEntityService entityService = Locator.getEntityService();
		Person entity = new Person();
		entity.firstName = "Sven";
		entity.lastName = "Fiergolla";
		entityService.save(getSession(), entity );
		
		item.addParticipant(entity, new Date(), null);
		genericItemService.save(getSession(), item);
		
		item = genericItemService.load(getSession(), item.id);
		assertNotNull(item.participations);
	}

}

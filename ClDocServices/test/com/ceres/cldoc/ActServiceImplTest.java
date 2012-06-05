package com.ceres.cldoc;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.cldoc.model.Person;

public class ActServiceImplTest extends TransactionalTest {

	public void testListClassNames() {
		IActService actService = Locator.getActService();
		List<String> classNames = actService.listClassNames(getSession(), "");
		assertNotNull(classNames);
		assertTrue(!classNames.isEmpty());
	}
	
	public void testSubCatalog() {
		ICatalogService catalogService = Locator.getCatalogService();
		Collection<Catalog> catalogs = catalogService.loadList(getSession(), "CLDOC.MAIN");
		assertNotNull(catalogs);
		assertTrue(!catalogs.isEmpty());
	}
	
	public void testSave() {
		IActService actService = Locator.getActService();
		
		Act act = new Act("Unknown");
		
		actService.save(getSession(), act);
		Date d = new Date(0l);
		act.date = d;
		actService.save(getSession(), act);
		assertEquals(d, act.date);
		
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
		
		act.set("string", "asdf");
		act.set("long", 1l);
		act.set("date", new Date());
		act.set("catalog", c1);
		actService.save(getSession(), act);
	
		CatalogList vl = new CatalogList();
		vl.addValue(c0);
		vl.addValue(c1);
		vl.addValue(c2);
		act.set("valuelist", vl);
		actService.save(getSession(), act);
		
		act = actService.load(getSession(), act.id);
		CatalogList cl2 = act.getCatalogList("valuelist");
		assertNotNull(cl2);
		assertEquals(vl.list.size(), cl2.list.size());
		
		act.set("string", "asdf2");
		act.set("long", 2l);
		act.set("date", new Date());
		act.set("catalog", c2);
	
		actService.save(getSession(), act);
		
		act = actService.load(getSession(), act.id);
		assertEquals("asdf2", act.getString("string"));
		assertEquals(new Long(2l), act.getLong("long"));
		assertEquals(c2.code, act.getCatalog("catalog").code);
		
		IEntityService entityService = Locator.getEntityService();
		Person entity = new Person();
		entity.firstName = "Sven";
		entity.lastName = "Fiergolla";
		entity.name = "Sven Fiergolla";
		entityService.save(getSession(), entity );

		act.addParticipant(entity, Catalog.PATIENT, new Date(), null);
		actService.save(getSession(), act);
		
		act = actService.load(getSession(), act.id);
		assertNotNull(act.participations);
		
		List<LogEntry> logEntries = Locator.getLogService().listRecent(getSession());
		assertFalse(logEntries.isEmpty());
	}

	public void testDataTypes() {
		Act act = new Act("Unknown");
		act.set("string", "asdf");
		act.set("long", 1l);
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND, 0);
		
		act.set("date", now.getTime());

		Catalog c1 = new Catalog();
		c1.parent = null;
		c1.code = "Quelle";
		c1.shortText = "kurz";
		c1.text = "lang";
		Locator.getCatalogService().save(getSession(), c1);
		
		act.set("catalog", c1);
		act.set("float", 5.2f);

		Locator.getActService().save(getSession(), act);		
		act = Locator.getActService().load(getSession(), act.id);
		
		assertEquals(5.2f, act.getFloat("float"));
		assertEquals(now.getTime(), act.getDate("date"));
	}
	
}

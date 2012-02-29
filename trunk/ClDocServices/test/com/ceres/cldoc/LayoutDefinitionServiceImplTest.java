package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.LayoutDefinition;


public class LayoutDefinitionServiceImplTest extends TransactionalTest {

	public void testAll() throws InterruptedException {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		IGenericItemService gis = Locator.getGenericItemService();
		
		LayoutDefinition ld = new LayoutDefinition();
		GenericItem item = new GenericItem("TESTCLASS");
		gis.save(getSession(), item);
		ld.name = "TESTCLASS";
		ld.xmlLayout = "<asdf/>";
		lds.save(getSession(), ld);
		Thread.sleep(500);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		
		assertNotNull(ld.id);
		
		List<LayoutDefinition> defs = lds.listLayoutDefinitions(getSession(), "T");
		assertNotNull(defs);
		assertTrue(!defs.isEmpty());
		
		LayoutDefinition def = lds.load(getSession(), "TESTCLASS");
		assertNotNull(def);

		
	}
}

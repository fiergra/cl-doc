package com.ceres.cldoc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.LayoutDefinition;


public class LayoutDefinitionServiceImplTest extends TransactionalTest {

	public void testExport() throws InterruptedException, IOException {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		byte[] bytes = lds.exportZip(getSession());

		File f = File.createTempFile("layout.", ".zip");
		System.out.println(f.getAbsolutePath());
		FileOutputStream w = new FileOutputStream(f);
		w.write(bytes);
		w.close();
		
		InputStream in = new ByteArrayInputStream(bytes);
		lds.importZip(getSession(), in);
	}
	
	public void testAll() throws InterruptedException {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		IActService actService = Locator.getActService();
		
		ActClass actClass = new ActClass(null, "TESTCLASS", null, false);
		LayoutDefinition ld = new LayoutDefinition(actClass, LayoutDefinition.FORM_LAYOUT, "<asdf/>");
		lds.save(getSession(), ld);
		
		Act act = new Act(new ActClass("TESTCLASS"));
		actService.save(getSession(), act);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		
		assertNotNull(ld.id);
		
		List<ActClass> classNames = actService.listClasses(getSession(), "T");
		assertNotNull(classNames);
		assertTrue(!classNames.isEmpty());
		
		LayoutDefinition def = lds.load(getSession(), "TESTCLASS", LayoutDefinition.FORM_LAYOUT);
		assertNotNull(def);

		
	}
}

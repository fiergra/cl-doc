package com.ceres.cldoc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LayoutDefinition;


public class LayoutDefinitionServiceImplTest extends TransactionalTest {

	public void testExport() throws InterruptedException, IOException {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		byte[] bytes = lds.exportZip(getSession(), LayoutDefinition.FORM_LAYOUT);

		File f = File.createTempFile("layout.", ".zip");
		System.out.println(f.getAbsolutePath());
		FileOutputStream w = new FileOutputStream(f);
		w.write(bytes);
		w.close();
		
		InputStream in = new ByteArrayInputStream(bytes);
		lds.importZip(getSession(), LayoutDefinition.FORM_LAYOUT, in);
	}
	
	public void testAll() throws InterruptedException {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		IActService actService = Locator.getActService();
		
		LayoutDefinition ld = new LayoutDefinition(LayoutDefinition.FORM_LAYOUT, "TESTCLASS", "<asdf/>");
		Act act = new Act("TESTCLASS");
		actService.save(getSession(), act);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		lds.save(getSession(), ld);
		Thread.sleep(500);
		
		assertNotNull(ld.id);
		
		List<String> classNames = actService.listClassNames(getSession(), "T");
		assertNotNull(classNames);
		assertTrue(!classNames.isEmpty());
		
		LayoutDefinition def = lds.load(getSession(), "TESTCLASS", LayoutDefinition.FORM_LAYOUT);
		assertNotNull(def);

		
	}
}

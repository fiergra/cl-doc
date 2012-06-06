package com.ceres.cldoc;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.ceres.cldoc.model.Catalog;

public class CatalogServiceImplTest extends TransactionalTest4 {

	

	@Test
	public void testConfig() {
		ICatalogService catalogService = Locator.getCatalogService();
		Collection<Catalog> list = catalogService.loadList(getSession(), "CLDOC.MAIN");
		Assert.assertNotNull(list);
	}

	@Test
	public void testImportExport() {
		ICatalogService catalogService = Locator.getCatalogService();
		String xml0 = catalogService.exportXML(getSession(), null);
		String xml1 = catalogService.exportXML(getSession(), null);
		Assert.assertEquals(xml1, xml0);
		catalogService.importXML(getSession(), new ByteArrayInputStream(xml1.getBytes()));
		String xml2 = catalogService.exportXML(getSession(), null);
		Assert.assertEquals(xml1, xml2);
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
	
}
package com.ceres.cldoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import junit.framework.TestCase;

public class DocArchiveTest extends TestCase {

	private static final String loremIpsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
	
	public void testStore() throws Exception {
		File file = File.createTempFile("junit", ".txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		w.write(loremIpsum);
		w.close();
		
		long docId = Locator.getDocArchive().store(file.getName(), new FileInputStream(file), null);

		byte[] doc = Locator.getDocArchive().retrieve(docId);
		assertNotNull(doc);
		String retrieved = new String(doc);
		assertEquals(loremIpsum, retrieved);
		
	}

}

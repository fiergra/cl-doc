package com.ceres.cldoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

public class DocArchiveTest extends TestCase {

	private static final String loremIpsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

	private File createTestFile(String content) throws IOException {
		File file = File.createTempFile("junit", ".txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(file));
		w.write(content);
		w.close();
		
		return file;
	}
	
	public void testStore() throws Exception {
		File file = createTestFile(loremIpsum);
		long docId = Locator.getDocArchive().store(file.getName(), new FileInputStream(file), null);

		byte[] doc = Locator.getDocArchive().retrieve(docId);
		assertNotNull(doc);
		String retrieved = new String(doc);
		assertEquals(loremIpsum, retrieved);
		
	}

	
	public void testSetPath() throws Exception {
		IDocArchive docArchive = Locator.getDocArchive();

		File file1 = createTestFile("asdf");
		File file2 = createTestFile("qwerty");
		
		long docId1 = docArchive.store(file1, null);
		byte[] rf1 = docArchive.retrieve(docId1);
		assertEquals("asdf", new String(rf1));

		docArchive.setPath("\\", true);
		long docId2 = docArchive.store(file2, null);
		
		byte[] rf2 = docArchive.retrieve(docId2);
		assertEquals("qwerty", new String(rf2));
		rf1 = docArchive.retrieve(docId1);
		assertEquals("asdf", new String(rf1));
	}

}

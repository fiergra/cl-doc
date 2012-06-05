package com.ceres.cldoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;

public class DocArchive implements IDocArchive {

	private File path;
	private static long docId = 0;
	private static Logger log = Logger.getLogger(DocArchive.class.getCanonicalName());
	
	public DocArchive() {
		String pathName = System.getProperty("user.dir") + File.separator + "DocArchive";
		path = new File(pathName);
		path.mkdirs();
		String[] names = path.list();
		for (String name:names) {
			try {
				if (name.endsWith(".data")) {
					int index = name.indexOf('.');
					if (index != -1) {
						long nameId = Long.parseLong(name.substring(0, index));
						if (nameId >= docId) {
							docId = nameId + 1;
						}
					}
				}
			} catch (RuntimeException rx) {
				log.warning(rx.toString());
			}
		}
		log.info("init doc archive at " + path + " starting with id " + docId);
	}
	
	@Override
	public long store(String name, InputStream data, HashMap<String, Serializable> metaData) throws IOException {
		File file = getFile(name);
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int read = data.read(buffer);
		while (read > 0) {
			out.write(buffer, 0, read);
			read = data.read(buffer);
		}
		out.close();
		return docId++;
	}

	private File getFile(String name) {
		File file = new File(path, String.valueOf(docId) + "." + name + ".data");
		return file;
	}

	private File getFile(final long id) {
		File[] files = path.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File file, String name) {
				return name.startsWith(id + ".");
			}
		});
		
		return files[0];
	}

	@Override
	public byte[] retrieve(long docId) throws IOException {
		InputStream in = retrieveData(docId);
		byte[] data = null;
		if (in != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = in.read(buffer);
			while (read > 0) {
				out.write(buffer, 0, read);
				read = in.read(buffer);
			}
			data = out.toByteArray();
		}
		return data;
	}

	private InputStream retrieveData(long docId) throws IOException {
		File file = getFile(docId);
		FileInputStream in = null;
		
		if (file != null) {
			in = new FileInputStream(file);
		}
		return in;
	}

	@Override
	public HashMap<String, Serializable> retrieveMetaData(long docId) {
		// TODO Auto-generated method stub
		return null;
	}

}

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
	private long docId = 0;
	private static Logger log = Logger.getLogger(DocArchive.class.getCanonicalName());
	
	public DocArchive() {
		String pathName = System.getProperty("user.dir") + File.separator + "DocArchive";
		init(pathName);
	}

	private void init(String pathName) {
		docId = 0;
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
		
		return files != null && files.length > 0 ? files[0] : null;
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

	@Override
	public String getFileName(long docId) {
		File file = getFile(docId);
		String name = null;
		if (file != null) {
			name = file.getName();
			int firstDot = name.indexOf('.');
			if (firstDot > 0) {
				name = name.substring(firstDot + 1);
			}
			
			if (name.endsWith(".data")) {	
				name = name.substring(0, name.length() - ".data".length());
			}
		}
		return name;
	}

	@Override
	public long store(File file, HashMap<String, Serializable> metaData) throws IOException {
		return store(file.getName(), new FileInputStream(file), metaData);
	}

	@Override
	public long store(File file) throws IOException {
		return store(file, null);
	}

	@Override
	public void setPath(String pathName, boolean copy) throws IOException {
		File newPath = new File(pathName);
		newPath.mkdirs();
		
		if (copy) {
			File[] files = path.listFiles();
			for (File f:files) {
				FileInputStream in = new FileInputStream(f);
				File newFile = new File(newPath.getAbsolutePath() + File.separator + f.getName());
				FileOutputStream out = new FileOutputStream(newFile);
				log.info("copy " + f.getAbsolutePath() + " to " + newFile.getAbsolutePath());
				byte[] buffer = new byte[4096];
				int read = in.read(buffer);
				while (read > 0) {
					out.write(buffer, 0, read);
					read = in.read(buffer);
				}
				in.close();
				out.close();
			}
		}
		
		init(pathName);
	}

	@Override
	public File getArchivePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setArchivePath(File archivePath) {
		// TODO Auto-generated method stub
		
	}

}

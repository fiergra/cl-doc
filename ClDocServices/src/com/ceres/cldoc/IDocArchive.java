package com.ceres.cldoc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

public interface IDocArchive {
	byte[] retrieve(long docId) throws IOException;
	HashMap<String, Serializable> retrieveMetaData(long docId);
	String getFileName(long docId);

	long store(String name, InputStream data, HashMap<String, Serializable> metaData) throws IOException;
	long store(File file, HashMap<String, Serializable> metaData) throws IOException;
	long store(File file) throws IOException;
	void setPath(String pathName, boolean copy) throws IOException;
	File getArchivePath();
	void setArchivePath(File archivePath);
}

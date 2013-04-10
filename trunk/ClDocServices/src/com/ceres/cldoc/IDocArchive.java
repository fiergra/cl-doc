package com.ceres.cldoc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;

public interface IDocArchive {
	long store(String name, InputStream data, HashMap<String, Serializable> metaData) throws IOException;
	byte[] retrieve(long docId) throws IOException;
	HashMap<String, Serializable> retrieveMetaData(long docId);
	
	File getArchivePath();
	void setArchivePath(File archivePath);
}

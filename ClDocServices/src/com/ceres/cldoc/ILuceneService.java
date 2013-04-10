package com.ceres.cldoc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Entity;

public interface ILuceneService {
	
	File getIndexPath();
	void setIndexPath(File path);
	
	void addToIndex(Entity entity, Act masterData) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, ParseException;
	List<Entity> retrieve(String criteria) throws CorruptIndexException, IOException, ParseException, ClassNotFoundException;
	void deleteIndex() throws CorruptIndexException, LockObtainFailedException, IOException;
}

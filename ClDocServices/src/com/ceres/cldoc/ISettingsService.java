package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.FileSystemNode;
import com.ceres.cldoc.model.ISession;

public interface ISettingsService {
	List<FileSystemNode> listFiles(String directory);
	
	void set(ISession session, String name, String value, Entity entity);
	String get(ISession session, String name, Entity entity);
}

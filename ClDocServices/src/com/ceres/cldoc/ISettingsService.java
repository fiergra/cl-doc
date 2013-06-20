package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.FileSystemNode;

public interface ISettingsService {
	List<FileSystemNode> listFiles(String directory);
	
	void set(Session session, String name, String value, Entity entity);
	String get(Session session, String name, Entity entity);
}

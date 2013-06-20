package com.ceres.cldoc.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ceres.cldoc.model.FileSystemNode;

public class Files {
	
	public static List<FileSystemNode> list(String parent) {
		File path = new File(parent);
		return list(path);
	}

	public static List<FileSystemNode> list(File file) {
		List<FileSystemNode> list = null;
		if (file.isDirectory()) {
			list = new ArrayList<FileSystemNode>();
			for (File f:file.listFiles()) {
				list.add(
						new FileSystemNode(
								f.getAbsolutePath(), f.getName(), 
								f.isDirectory(), list(f), f.canRead(), f.canWrite(), f.canExecute()));
			}
		}
		return list;
	}
}

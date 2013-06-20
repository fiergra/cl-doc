package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.List;

public class FileSystemNode implements HasChildren<FileSystemNode>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -333579067618508002L;
	public String name;
	public String absolutePath;
	public boolean isDirectory;
	public List<FileSystemNode> children;
	public boolean r;
	public boolean w;
	public boolean x;
	
	public FileSystemNode(){};
	
	public FileSystemNode(String absolutePath, String name, boolean isDirectory, List<FileSystemNode> children, boolean r, boolean w, boolean x) {
		this.absolutePath = absolutePath;
		this.name = name;
		this.isDirectory = isDirectory;
		this.children = children;
		this.r = r;
		this.w = w;
		this.x = x;
	}

	@Override
	public boolean hasChildren() {
		return isDirectory;
	}

	@Override
	public List<FileSystemNode> getChildren() {
		return children;
	}
}

package com.ceres.cldoc.model;

import java.util.List;

public interface HasChildren <T> {
	boolean hasChildren();
	List<T> getChildren();
}

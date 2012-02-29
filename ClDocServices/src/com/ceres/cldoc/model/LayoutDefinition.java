package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class LayoutDefinition implements Serializable {

	private static final long serialVersionUID = 7581049677245652840L;
	public Long id;
	public String name;
	public String xmlLayout;
	public Date validTo;
}

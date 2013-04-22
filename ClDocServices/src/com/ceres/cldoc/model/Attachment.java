package com.ceres.cldoc.model;

import java.io.Serializable;

public class Attachment implements Serializable {

	private static final long serialVersionUID = -3041995395156429044L;

	public Long id;
	public Act act;
	public String filename;
	public String description;
	public long docId;
	public int type;

	public Attachment(){}
	
	public Attachment(Long id, String name, String description, int type) {
		super();
		this.id = id;
		this.filename = name;
		this.type = type;
	}

}

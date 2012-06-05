package com.ceres.cldoc.model;

import java.io.Serializable;
import java.util.Date;

public class LogEntry implements Serializable {
	private static final long serialVersionUID = 928654644402757828L;

	public long id;
	public int type;
	public Act act;
	public Entity entity;
	public String logEntry;
	public Date logDate;
	
	public LogEntry(){}
	
	public LogEntry(long id, int type, Act act, Entity entity, String logEntry, Date logDate) {
		super();
		this.id = id;
		this.type = type;
		this.act = act;
		this.entity = entity;
		this.logEntry = logEntry;
		this.logDate = logDate;
	}
	
	
}

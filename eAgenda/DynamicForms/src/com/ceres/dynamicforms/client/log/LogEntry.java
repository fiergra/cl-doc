package com.ceres.dynamicforms.client.log;

import java.io.Serializable;
import java.util.logging.Level;

public class LogEntry implements Serializable {

	private static final long serialVersionUID = -5144726803212957248L;
	
	public String text;
	public long time;
	public Level level;
	
	protected LogEntry() {}

	public LogEntry(Level level, String text) {
		this.level = level;
		this.text = text;
		this.time = System.currentTimeMillis();
	}
	

}

package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LogEntry;

public interface ILogService {
	final static int INSERT = 1;
	final static int UPDATE = 2;
	final static int VIEW = 3;

	void log(Session session, int type, Act act, String logEntry);
	List<LogEntry> listRecent(Session session);
}

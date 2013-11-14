package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.core.ISession;

public interface ILogService {
	final static int INSERT = 1;
	final static int UPDATE = 2;
	final static int VIEW = 3;
	static final int ADD_ATTACHMENT = 10;
	static final int DELETE_ATTACHMENT = 11;

	void log(ISession session, int type, Act act, String logEntry);
	List<LogEntry> listRecent(ISession session);
}

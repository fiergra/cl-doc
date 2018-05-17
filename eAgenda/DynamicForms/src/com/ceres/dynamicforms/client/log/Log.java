package com.ceres.dynamicforms.client.log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.ceres.dynamicforms.client.ResultCallback;

public class Log {
	private static final List<LogEntry> logEntries = new ArrayList<>();
	private static final List<ResultCallback<LogEntry>> listeners = new ArrayList<>();
	
	public static void info(String text) {
		log(Level.INFO, text);
	}
	
	public static void warn(String text) {
		log(Level.WARNING, text);
	}
	
	public static void severe(String text) {
		log(Level.SEVERE, text);
	}
	
	public static void log(Level level, String text) {
		LogEntry le = new LogEntry(level, text);
		logEntries.add(0, le);
		notifyListeners(le);
	}
	
	private static void notifyListeners(LogEntry le) {
		for (ResultCallback<LogEntry> l:listeners) {
			l.callback(le);
		}
	}

	public static void register(ResultCallback<LogEntry> listener) {
		listeners.add(listener);
	}
	
	public static void unregister(ResultCallback<LogEntry> listener) {
		listeners.remove(listener);
	}
	
	public static Iterator<LogEntry> getEntries() {
		return logEntries.iterator();
	}
	
}

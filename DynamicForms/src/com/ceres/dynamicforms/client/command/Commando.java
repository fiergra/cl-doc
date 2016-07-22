package com.ceres.dynamicforms.client.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Commando {
	private static List<ICommand> commands = new ArrayList<>();
	private static int commandIndex = -1;
	private static Logger logger = Logger.getLogger("Commando");
	
	private static List<Runnable> indexChangeListeners = new ArrayList<>();

	interface ExecutionListener {
		void executed(ICommand command);
	}
	
	public static void addIndexChangeListener(Runnable listener) {
		indexChangeListeners.add(listener);
	}

	private static List<ExecutionListener> executionListeners = new ArrayList<>();
	
	public static void addExecutionListener(ExecutionListener listener) {
		executionListeners.add(listener);
	}

	
	
	public static void execute(final ICommand command) {
		logger.info("exec '" + command.getDescription() + "'");
		command.exec();
		commandIndex = commands.size();
		commands.add(command);
		notifyIndexChangeListeners();
		notifyExecutionListeners(command);
	}
	
	public static boolean canUndo() {
		return getUndoCommand() != null;
	}

	public static boolean canRedo() {
		return getRedoCommand() != null;
	}
	
	public static ICommand getUndoCommand() {
		return commandIndex >= 0 ? commands.get(commandIndex) : null;
	}

	public static ICommand getRedoCommand() {
		return commandIndex < commands.size() - 1 ? commands.get(commandIndex + 1) : null;
	}
	
	public static void undo() {
		ICommand command = getUndoCommand();
		if (command != null) {
			logger.info("undo '" + command.getDescription() + "'");
			command.undo();
			decrement();
		}
	}

	public static void redo() {
		ICommand command = getRedoCommand();
		if (command != null) {
			logger.info("redo '" + command.getDescription() + "'");
			command.exec();
			increment();
		}
	}
	
	private static void increment() {
		commandIndex++;
		notifyIndexChangeListeners();
	}
	
	private static void decrement() {
		commandIndex--;
		notifyIndexChangeListeners();
	}
	
	private static void notifyIndexChangeListeners() {
		for (Runnable r:indexChangeListeners) {
			r.run();
		}
	}

	private static void notifyExecutionListeners(ICommand command) {
		for (ExecutionListener r:executionListeners) {
			r.executed(command);
		}
	}

	
}

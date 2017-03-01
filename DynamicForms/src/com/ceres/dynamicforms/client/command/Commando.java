package com.ceres.dynamicforms.client.command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Commando {
	private List<ICommand> commands = new ArrayList<>();
	private int commandIndex = -1;
	private Logger logger = Logger.getLogger("Commando");
	
	private List<Runnable> indexChangeListeners = new ArrayList<>();

	interface ExecutionListener {
		void executed(ICommand command);
	}
	
	public void addIndexChangeListener(Runnable listener) {
		indexChangeListeners.add(listener);
	}

	private List<ExecutionListener> executionListeners = new ArrayList<>();
	
	public void addExecutionListener(ExecutionListener listener) {
		executionListeners.add(listener);
	}

	
	
	public void execute(final ICommand command) {
		logger.info("exec '" + command.getDescription() + "'");
		command.exec();
		if (commandIndex < commands.size() - 1) {
			commands = new ArrayList<>(commands.subList(0, commandIndex + 1));
		}
		commandIndex = commands.size();
		commands.add(command);
		notifyIndexChangeListeners();
		notifyExecutionListeners(command);
	}
	
	public boolean canUndo() {
		return getUndoCommand() != null;
	}

	public boolean canRedo() {
		return getRedoCommand() != null;
	}
	
	public ICommand getUndoCommand() {
		return commandIndex >= 0 ? commands.get(commandIndex) : null;
	}

	public ICommand getRedoCommand() {
		return commandIndex < commands.size() - 1 ? commands.get(commandIndex + 1) : null;
	}
	
	public void undo() {
		ICommand command = getUndoCommand();
		if (command != null) {
			logger.info("undo '" + command.getDescription() + "'");
			command.undo();
			decrement();
		}
	}

	public void redo() {
		ICommand command = getRedoCommand();
		if (command != null) {
			logger.info("redo '" + command.getDescription() + "'");
			command.exec();
			increment();
		}
	}
	
	private void increment() {
		commandIndex++;
		notifyIndexChangeListeners();
	}
	
	private void decrement() {
		commandIndex--;
		notifyIndexChangeListeners();
	}
	
	private void notifyIndexChangeListeners() {
		for (Runnable r:indexChangeListeners) {
			r.run();
		}
	}

	private void notifyExecutionListeners(ICommand command) {
		for (ExecutionListener r:executionListeners) {
			r.executed(command);
		}
	}

	
}

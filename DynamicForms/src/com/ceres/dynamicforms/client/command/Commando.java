package com.ceres.dynamicforms.client.command;

import java.util.ArrayList;
import java.util.List;

public class Commando {
	private static List<ICommand> commands = new ArrayList<>();
	private static int commandIndex = -1;
	
	public static void execute(ICommand command) throws Exception {
		command.exec();
		commandIndex = commands.size();
		commands.add(command);
	}
	
	public static ICommand canUndo() {
		return commandIndex >= 0 ? commands.get(commandIndex) : null;
	}

	public static ICommand canRedo() {
		return commandIndex < commands.size() - 1 ? commands.get(commandIndex + 1) : null;
	}
	
	public static boolean undo() throws Exception {
		ICommand command = canUndo();
		if (command != null) {
			command.undo();
			commandIndex--;
		}
		return command != null;
	}

	public static boolean redo() throws Exception {
		ICommand command = canRedo();
		if (command != null) {
			command.exec();
			commandIndex++;
		}
		return command != null;
	}
}

package com.ceres.dynamicforms.client.command;

public interface ICommand {
	void exec();
	void undo();
	String getName();
	String getDescription();
}

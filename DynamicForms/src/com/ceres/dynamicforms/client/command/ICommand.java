package com.ceres.dynamicforms.client.command;

public interface ICommand {
	void exec() throws Exception;
	void undo() throws Exception;
	String getName();
	String getDescription();
}

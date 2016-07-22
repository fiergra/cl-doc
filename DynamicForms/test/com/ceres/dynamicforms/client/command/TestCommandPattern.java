package com.ceres.dynamicforms.client.command;

import junit.framework.TestCase;

public class TestCommandPattern extends TestCase {
	
	private int sum = 0;
	
	public void testUndoRedo() throws Exception {
	
		class AddCommand extends AbstractCommand {

			private int value;

			public AddCommand(int value) {
				super("add", "add " + value + " to the sum");
				this.value = value;
			}

			@Override
			public void exec() {
				sum += value;
			}

			@Override
			public void undo() {
				sum -= value;
			}
			
		}
		
		
		Commando.execute(new AddCommand(10));
		assertEquals(10, sum);
		Commando.execute(new AddCommand(1));
		assertEquals(11, sum);
		Commando.execute(new AddCommand(9));
		assertEquals(20, sum);
		Commando.undo();
		assertEquals(11, sum);
		Commando.redo();
		assertEquals(20, sum);
		Commando.undo();
		Commando.undo();
		assertEquals(10, sum);
		Commando.undo();
		assertEquals(0, sum);
		assertFalse(Commando.canUndo());
		
		while (Commando.canRedo()) { Commando.redo(); }
		assertEquals(20, sum);
		
	}

}

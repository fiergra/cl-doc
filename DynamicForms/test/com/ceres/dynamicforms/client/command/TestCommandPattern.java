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
			public void exec() throws Exception {
				sum += value;
			}

			@Override
			public void undo() throws Exception {
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
		assertFalse(Commando.undo());
		
		while (Commando.redo()) {}
		assertEquals(20, sum);
		
	}

}

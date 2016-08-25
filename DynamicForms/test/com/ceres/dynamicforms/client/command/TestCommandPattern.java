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
				System.out.print(sum + " + " + value + " = ");
				sum += value;
				System.out.println(sum);
			}

			@Override
			public void undo() {
				System.out.print(sum + " - " + value + " = ");
				sum -= value;
				System.out.println(sum);
			}
			
			
			
		}
		
		
		Commando.execute(new AddCommand(1));
		assertEquals(1, sum);
		Commando.execute(new AddCommand(2));
		assertEquals(3, sum);
		Commando.execute(new AddCommand(3));
		assertEquals(6, sum);
		Commando.undo();
		assertEquals(3, sum);
		Commando.redo();
		assertEquals(6, sum);
		Commando.undo();
		Commando.undo();
		assertEquals(1, sum);
		Commando.undo();
		assertEquals(0, sum);
		assertFalse(Commando.canUndo());
		
		while (Commando.canRedo()) { Commando.redo(); }
		assertEquals(6, sum);

		Commando.execute(new AddCommand(4));
		assertEquals(10, sum);
		Commando.undo();
		assertEquals(6, sum);
		Commando.execute(new AddCommand(5));
		assertEquals(11, sum);
		assertFalse(Commando.canRedo());

		System.out.println("undo all");
		while (Commando.canUndo()) { 
			Commando.undo(); 
		}
		assertEquals(0, sum);
		System.out.println("redo all");
		while (Commando.canRedo()) { 
			Commando.redo(); 
		}
		assertEquals(11, sum);

		
	}

}

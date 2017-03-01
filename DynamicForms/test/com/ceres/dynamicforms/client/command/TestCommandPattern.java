package com.ceres.dynamicforms.client.command;

import junit.framework.TestCase;

public class TestCommandPattern extends TestCase {
	
	private int sum = 0;
	
	public void testUndoRedo() throws Exception {
		Commando commando = new Commando();
		
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
		
		
		commando.execute(new AddCommand(1));
		assertEquals(1, sum);
		commando.execute(new AddCommand(2));
		assertEquals(3, sum);
		commando.execute(new AddCommand(3));
		assertEquals(6, sum);
		commando.undo();
		assertEquals(3, sum);
		commando.redo();
		assertEquals(6, sum);
		commando.undo();
		commando.undo();
		assertEquals(1, sum);
		commando.undo();
		assertEquals(0, sum);
		assertFalse(commando.canUndo());
		
		while (commando.canRedo()) { commando.redo(); }
		assertEquals(6, sum);

		commando.execute(new AddCommand(4));
		assertEquals(10, sum);
		commando.undo();
		assertEquals(6, sum);
		commando.execute(new AddCommand(5));
		assertEquals(11, sum);
		assertFalse(commando.canRedo());

		System.out.println("undo all");
		while (commando.canUndo()) { 
			commando.undo(); 
		}
		assertEquals(0, sum);
		System.out.println("redo all");
		while (commando.canRedo()) { 
			commando.redo(); 
		}
		assertEquals(11, sum);

		
	}

}

package com.ceres.dynamicforms.client.command;

import com.ceres.dynamicforms.client.ITranslator;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;

public class CommandoButtons extends HorizontalPanel {
	
	private static String UNDO = "undo";
	private static String REDO = "redo";
	
	public static void addCommandoButtons(Panel panel, int size) {
		final PushButton pbUndo = new PushButton(new Image("assets/images/undo.png"));
		final PushButton pbRedo = new PushButton(new Image("assets/images/redo.png"));
		
		Commando.addIndexChangeListener(new Runnable() {
			
			@Override
			public void run() {
				ICommand undo = Commando.getUndoCommand();
				ICommand redo = Commando.getRedoCommand();
				
				if (undo != null) {
					pbUndo.setEnabled(true);
					pbUndo.setTitle(UNDO + ": " + undo.getDescription());
				} else {
					pbUndo.setEnabled(false);
				}
				
				if (redo != null) {
					pbRedo.setEnabled(true);
					pbRedo.setTitle(REDO + ": " + redo.getDescription());
				} else {
					pbRedo.setEnabled(false);
				}
			}
		});
		
		pbUndo.setEnabled(Commando.canUndo());
		pbRedo.setEnabled(Commando.canRedo());
		
		panel.add(pbUndo);
		panel.add(pbRedo);
		
		pbUndo.setPixelSize(size, size);
		pbUndo.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Commando.undo();
			}
		});
		
		
		pbRedo.setPixelSize(size, size);
		pbRedo.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Commando.redo();
			}
		});
		
		
	}
	
	public CommandoButtons(ITranslator translator) {
		addCommandoButtons(this, 24);
	}

}

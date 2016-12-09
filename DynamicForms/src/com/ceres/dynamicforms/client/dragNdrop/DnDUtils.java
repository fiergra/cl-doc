package com.ceres.dynamicforms.client.dragNdrop;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.user.client.ui.UIObject;

public class DnDUtils {


	public static <T> void supportDnD(final UIObject renderer, final T ou, final Acceptor acceptor, boolean drag, boolean drop) {
		HasAllDragAndDropHandlers hadndh = (HasAllDragAndDropHandlers) renderer;

		if (drag) {
			renderer.getElement().setDraggable(Element.DRAGGABLE_TRUE);
			hadndh.addDragStartHandler(new DragStartHandler() {
				
				@Override
				public void onDragStart(DragStartEvent event) {
					acceptor.setDragged(ou);
					System.out.println("drag start: " + ou);
				}
			});
			
			hadndh.addDragEndHandler(new DragEndHandler() {
				
				@Override
				public void onDragEnd(DragEndEvent event) {
					event.stopPropagation();
					event.preventDefault();
				}
			});
		}
		
		if (drop) {
			hadndh.addDropHandler(new DropHandler() {
				
				@Override
				public void onDrop(DropEvent event) {
//					DataTransfer dt = event.getDataTransfer();
//					System.out.println(dt);
					acceptor.drop(ou);
					event.preventDefault();
				}
			});
			
	
			hadndh.addDragOverHandler(new DragOverHandler() {
				
				@Override
				public void onDragOver(DragOverEvent event) {
					event.stopPropagation();
					event.preventDefault();
				}
			});
			
			hadndh.addDragEnterHandler(new DragEnterHandler() {
				
				@Override
				public void onDragEnter(DragEnterEvent event) {
					event.preventDefault();
				}
			});
	
	//		renderer.addDragLeaveHandler(new DragLeaveHandler() {
	//			
	//			@Override
	//			public void onDragLeave(DragLeaveEvent event) {
	//				event.preventDefault();
	//				System.out.println("drag leave");
	//			}
	//		});

		}
	}



	
}

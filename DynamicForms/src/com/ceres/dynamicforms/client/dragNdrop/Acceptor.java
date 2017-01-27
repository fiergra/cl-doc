package com.ceres.dynamicforms.client.dragNdrop;

public class Acceptor {
	static private Object dragged;

	public void setDragged(Object dragged) {
		Acceptor.dragged = dragged;
	};
	
	public Object getDragged() {
		return Acceptor.dragged;
	};
	
	public void drop(Object o) {}

	public boolean accepts(Object dragged) {
		return true;
	};
}

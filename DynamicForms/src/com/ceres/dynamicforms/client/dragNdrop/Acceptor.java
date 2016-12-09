package com.ceres.dynamicforms.client.dragNdrop;

public class Acceptor {
	static Object dragged;

	void setDragged(Object dragged) {
		Acceptor.dragged = dragged;
	};
	
	void drop(Object o) {};
}

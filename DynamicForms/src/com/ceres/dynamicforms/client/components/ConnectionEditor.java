package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConnectionEditor extends LayoutPanel {

	private Canvas canvas;

	// drag stuff
	private FocusPanel std = null;
	private int offsetX;
	private int offsetY;
	
	public ConnectionEditor() {
	}
	
	private void startDrag(FocusPanel sr, int relativeX, int relativeY) {
		offsetX = relativeX - sr.getAbsoluteLeft() ;
		offsetY = relativeY - sr.getAbsoluteTop();
		std = sr;
	}

	private void stopDrag(Widget sr) {
		if (std == sr) {
			std = null;
		};
	}

	
	public void clear() {
		for (Widget w:wrappers) {
			remove(w);
		}
		wrappers.clear();
		allEdges.clear();
	}
	
	public void eraseCanvas() {
		if (canvas != null) {
			canvas.getContext2d().clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
		}
	}
	
	private void initCanvas() {
		if (canvas == null) {
			canvas = Canvas.createIfSupported();
			if (canvas != null) {
				add(canvas);
				canvas.addMouseMoveHandler(new MouseMoveHandler() {
					
					@Override
					public void onMouseMove(MouseMoveEvent event) {
						if (std != null) {
							System.out.println("DRAG: " + event.getClientX() + "," + event.getY());
							eraseCanvas();
							setWidgetLeftWidth(std, event.getX() - offsetX, Unit.PX, std.getOffsetWidth(), Unit.PX);
							setWidgetTopHeight(std, event.getY() - offsetY, Unit.PX, std.getOffsetHeight(), Unit.PX);
							drawEdges();
						} else {
							System.out.println(event.getClientX() + "," + event.getY());
						}
					}
				});
	
				canvas.addMouseDownHandler(new MouseDownHandler() {
					
					@Override
					public void onMouseDown(MouseDownEvent event) {
						System.out.println(event);
					}
				});
	
				canvas.addMouseUpHandler(new MouseUpHandler() {
					
					@Override
					public void onMouseUp(MouseUpEvent event) {
						std = null;
					}
				});
			} else {
				add(new Label("insufficient HTML5 support..."));
			}
		}

		System.out.println(canvas.getOffsetHeight());
		if (canvas != null) {
				int w = getOffsetWidth();
				int h = getOffsetHeight();
				canvas.setPixelSize(w, h);
				setWidgetLeftRight(canvas, 0, Unit.PX, 0, Unit.PX);
				setWidgetTopBottom(canvas, 0, Unit.PX, 0, Unit.PX);
				canvas.setCoordinateSpaceHeight(h);
				canvas.setCoordinateSpaceWidth(w);
		}
	}

	private void drawEdges(String color) {
			for (Edge e : allEdges) {
				drawEdge(e, color);
			}
	}

	private void drawEdges() {
		drawEdges("black");
	}

	private class Edge {
		Widget from;
		Widget to;
		
		public Edge(Widget from, Widget to) {
			super();
			this.from = from;
			this.to = to;
		}
		
		
	}
	
	private final HashMap<Widget, List<Edge>> directedEdges = new HashMap<>();
	private final List<Edge> allEdges = new ArrayList<>();
	private final List<Widget> wrappers = new ArrayList<>();
	
	public void addEdge(Widget sFrom, Widget sTo) {
		Edge e = new Edge(sFrom, sTo);
		
		allEdges.add(e);
		addEdge(e.from, e);
		addEdge(e.to, e);
		
		drawEdge(e, "black");
	}

	private void addEdge(Widget w, Edge e) {
		List<Edge> es = directedEdges.get(w);
		if (es == null) {
			es = new ArrayList<>();
			directedEdges.put(w, es);
		}
		es.add(e);
	}

	
	private int xDiff(Widget w1, Widget w2) {
		int xdiff = ((w2.getAbsoluteLeft() + w2.getOffsetWidth()) / 2) - ((w1.getAbsoluteLeft() + w1.getOffsetWidth()) / 2);
		System.out.println("xdiff:" + xdiff);
		return xdiff;
	}
	
	private int yDiff(Widget w1, Widget w2) {
		return ((w2.getAbsoluteTop() + w2.getOffsetHeight()) / 2) - ((w1.getAbsoluteTop() + w1.getOffsetHeight()) / 2);
	}
	
	private void drawEdge(Edge e, String color) {

		if (canvas != null) {
			Context2d ctx = canvas.getContext2d(); 
			ctx.setStrokeStyle(color);
			
			if (xDiff(e.from, e.to) > e.to.getOffsetWidth()) {
				lineFromRightToLeft(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
			} else if (xDiff(e.to, e.from) > e.from.getOffsetWidth()) {
				lineFromLeftToRight(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
			} else {
				if (yDiff(e.from, e.to) > 10) {
					lineFromRightToTop(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
				} else if (yDiff(e.to, e.from) > 10) {
					lineFromRightToBottom(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
				}
			}
//			
//			if (e.to.getAbsoluteLeft() - 100 > (e.from.getAbsoluteLeft() + e.from.getOffsetWidth())) {
//				if ((e.from.getAbsoluteTop() + e.from.getOffsetHeight()) < e.to.getAbsoluteTop() - 100) {
//					lineFromRightToLeft(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
//				} else {
//					lineFromBottomToLeft(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
//				}
//			} else {
//				lineFromRightToTop(ctx, e, getAbsoluteLeft(), getAbsoluteTop());
//			}
		}

	}

	private void lineFromRightToLeft(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth();
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() / 2;
		
		int endX = e.to.getAbsoluteLeft() - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
//			ctx.bezierCurveTo(endX, startY, endX, startY, endX, endY);
			ctx.bezierCurveTo(endX, startY, startX, endY, endX, endY);
			ctx.stroke();
		}
		arrowHeadRight(endX, endY);
	}
	
	private void lineFromLeftToRight(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft);
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() / 2;
		
		int endX = e.to.getAbsoluteLeft() - absLeft + e.to.getOffsetWidth();;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
//			ctx.bezierCurveTo(endX, startY, endX, startY, endX, endY);
			ctx.bezierCurveTo(endX, startY, startX, endY, endX, endY);
			ctx.stroke();
		}
		arrowHeadLeft(endX, endY);
	}
	
	private void lineFromBottomToLeft(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() ;
		
		int endX = e.to.getAbsoluteLeft() - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
//			ctx.bezierCurveTo(endX, startY, endX, startY, endX, endY);
			ctx.bezierCurveTo(startX, endY, startX, endY, endX, endY);
			ctx.stroke();
		}
		arrowHeadRight(endX, endY);
	}
	

	
	private void lineFromRightToTop(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() + e.from.getOffsetWidth()) - absLeft;
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() / 2 ;
		
		int endX = (e.to.getAbsoluteLeft() + e.to.getOffsetWidth() / 2) - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(endX, startY, endX, startY, endX, endY);
//			ctx.bezierCurveTo(endX, startY, startX, endY, endX, endY);
			ctx.stroke();
		}
		arrowHeadDown(endX, endY);
	}
	
	
	private void lineFromRightToBottom(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() + e.from.getOffsetWidth()) - absLeft;
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() / 2 ;
		
		int endX = (e.to.getAbsoluteLeft() + e.to.getOffsetWidth() / 2) - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight();

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(endX, startY, endX, startY, endX, endY);
//			ctx.bezierCurveTo(endX, startY, startX, endY, endX, endY);
			ctx.stroke();
		}
		arrowHeadUp(endX, endY);
	}
	
	private void arrowHeadRight(int x, int y) {
		Context2d ctx = canvas.getContext2d(); 
		final int SIZE = 5;
		
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x - 2*SIZE, y - SIZE);
		ctx.lineTo(x - 2*SIZE, y + SIZE);
		ctx.lineTo(x,y);
		ctx.fill();
		ctx.closePath();
	}

	private void arrowHeadLeft(int x, int y) {
		Context2d ctx = canvas.getContext2d(); 
		final int SIZE = 5;
		
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x + 2*SIZE, y - SIZE);
		ctx.lineTo(x + 2*SIZE, y + SIZE);
		ctx.lineTo(x,y);
		ctx.fill();
		ctx.closePath();
	}

	private void arrowHeadDown(int x, int y) {
		Context2d ctx = canvas.getContext2d(); 
		final int SIZE = 5;
		
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x - SIZE, y - 2*SIZE);
		ctx.lineTo(x + SIZE, y - 2*SIZE);
		ctx.lineTo(x,y);
		ctx.fill();
		ctx.closePath();
	}

	private void arrowHeadUp(int x, int y) {
		Context2d ctx = canvas.getContext2d(); 
		final int SIZE = 5;
		
		ctx.beginPath();
		ctx.moveTo(x,y);
		ctx.lineTo(x - SIZE, y + 2*SIZE);
		ctx.lineTo(x + SIZE, y + 2*SIZE);
		ctx.lineTo(x,y);
		ctx.fill();
		ctx.closePath();
	}

	public void addWidget(Widget widget, int x, int y, int w, int h) {
		initCanvas();
		widget.setPixelSize(w, h);
		widget = createWrapper(widget, w, h); 
		add(widget);
		setWidgetLeftWidth(widget, x, Unit.PX, w, Unit.PX);
		setWidgetTopHeight(widget, y, Unit.PX, h, Unit.PX);
		forceLayout();
	}

	private Widget createWrapper(Widget widget, int w, int h) {
		final FocusPanel sr = new FocusPanel();
		wrappers.add(sr);
		sr.add(widget);
		sr.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				startDrag(sr, event.getClientX(), event.getClientY());
			}
		});
		
		sr.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				stopDrag(sr);
			}
		});
		
		sr.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				MouseMoveEvent.fireNativeEvent(event.getNativeEvent(), canvas, canvas.getElement());
			}
		});
		return sr;
	}


}

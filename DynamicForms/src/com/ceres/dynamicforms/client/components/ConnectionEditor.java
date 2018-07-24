package com.ceres.dynamicforms.client.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.ceres.dynamicforms.client.ResultCallback;
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

	private final HashMap<Widget, Widget> wrappers = new HashMap<>();
	private final List<Edge> allEdges = new ArrayList<>();
//	private final List<Widget> wrappers = new ArrayList<>();


	private Canvas canvas;

	// drag stuff
	private FocusPanel std = null;
	private int offsetX;
	private int offsetY;
	
	private ResultCallback<Edge> onClickEdge;
	
	public ConnectionEditor() {
	}
	
	
	
	public void setOnClickEdge(ResultCallback<Edge> onClickEdge) {
		this.onClickEdge = onClickEdge;
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
		wrappers.values().forEach(w -> remove(w));
		wrappers.clear();
		allEdges.forEach(e -> remove(e.l));
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
	
//				canvas.addMouseDownHandler(new MouseDownHandler() {
//					
//					@Override
//					public void onMouseDown(MouseDownEvent event) {
//						System.out.println(event);
//					}
//				});
//	
//				canvas.addMouseUpHandler(new MouseUpHandler() {
//					
//					@Override
//					public void onMouseUp(MouseUpEvent event) {
//						std = null;
//					}
//				});
			} else {
				add(new Label("insufficient HTML5 support..."));
			}
		}

		updateCanvasSize();
	}

	private void updateCanvasSize() {
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

	public class Edge {
		public final Widget from;
		public final Widget to;
		public final Label l;
		public final Object payLoad;
		
		public Edge(Widget from, Widget to, String text, Object payLoad) {
			super();
			this.payLoad = payLoad;
			this.from = from;
			this.to = to;
			this.l = new Label(text);
			this.l.addClickHandler(e -> {
				e.stopPropagation();
				if (onClickEdge != null) {
					onClickEdge.callback(this);
				}
			});
			
			this.l.addMouseOverHandler(e -> drawEdge(this, "red", 3D));
			this.l.addMouseOutHandler(e -> {
				refresh();
			});
			this.l.setStyleName("workflowActionLabel");
		}
		
		
	}
	
	public void addEdge(Widget sFrom, Widget sTo, String label) {
		addEdge(sFrom, sTo, label, null);
	}

	public void refresh() {
		eraseCanvas();
		drawEdges();
	}


	public void removeWidget(Widget w) {
		remove(wrappers.get(w));
		List<Edge> filtered = allEdges.stream().filter(e -> e.from == w || e.to == w).collect(Collectors.toList());
		filtered.forEach(e -> removeEdge(e));
	}


	public void removeEdge(Edge e) {
		remove(e.l);
		allEdges.remove(e);
	}
	
	public void addEdge(Widget sFrom, Widget sTo, String label, Object payLoad) {
		Edge e = new Edge(sFrom, sTo, label, payLoad);
		
		add(e.l);
		setWidgetLeftWidth(e.l, 0, Unit.PX, 0, Unit.PX);
		
		allEdges.add(e);
//		addEdge(e.from, e);
//		addEdge(e.to, e);
		
		drawEdge(e, "black");
	}

//	private void addEdge(Widget w, Edge e) {
//		List<Edge> es = directedEdges.get(w);
//		if (es == null) {
//			es = new ArrayList<>();
//			directedEdges.put(w, es);
//		}
//		es.add(e);
//	}

	private void drawEdge(Edge e, String color) {
		drawEdge(e, color, null);
	}
	
	private void drawEdge(Edge e, String color, Double width) {

		if (canvas != null) {
			Context2d ctx = canvas.getContext2d(); 
			
			ctx.setStrokeStyle(color);
			ctx.setFillStyle(color);
			
			if (width != null) {
				ctx.setLineWidth(width);
			} else {
				ctx.setLineWidth(1);
			}
			ctx.setFont("0.7em Arial");
			int absLeft = getAbsoluteLeft();
			int absTop = getAbsoluteTop();
			
			if (e.from == e.to) {
				selfReference(ctx, e, absLeft, absTop);
			} else if (isLeftOf(e.from, e.to) ) {
				lineFromRightToLeft(ctx, e, absLeft, absTop);
			} else if (isRightOf(e.from, e.to)) {
				lineFromLeftToRight(ctx, e, absLeft, absTop);
			} else if (isHalfLeftOf(e.from, e.to)) {
				if (isAbove(e.from, e.to)) {
					lineFromBottomToLeft(ctx, e, absLeft, absTop);
				} else if (isBelow(e.from, e.to)) {
					lineFromTopToLeft(ctx, e, absLeft, absTop);
				} else {
					lineFromRightToLeft(ctx, e, absLeft, absTop);
				}
			} else if (isHalfRightOf(e.from, e.to)) {
				if (isAbove(e.from, e.to)) {
					lineFromBottomToRight(ctx, e, absLeft, absTop);
				} else if (isBelow(e.from, e.to)) {
					lineFromTopToRight(ctx, e, absLeft, absTop);
				} else {
					lineFromLeftToRight(ctx, e, absLeft, absTop);
				}
			} else {
				if (isAbove(e.from, e.to)) {
					lineFromBottomToTop(ctx, e, absLeft, absTop);
				} else if (isBelow(e.from, e.to)) {
					lineFromTopToBottom(ctx, e, absLeft, absTop);
				}
			}
		}

	}

	private void selfReference(Context2d ctx, Edge e, int absLeft, int absTop) {
		int cornerX = e.from.getAbsoluteLeft() - absLeft;
		int cornerY = e.from.getAbsoluteTop() - absTop;
		
		ctx.beginPath();
		ctx.arc(cornerX, cornerY, 20, 0.5 * Math.PI, 2 * Math.PI);
		ctx.stroke();	
		arrowHeadDown(cornerX + 20, cornerY);
		moveLabel(e.l, cornerX + 20, cornerY - 30, cornerX + 20, cornerY - 30);
	}

	private boolean isAbove(Widget from, Widget to) {
		return from.getAbsoluteTop() + from.getOffsetHeight() < to.getAbsoluteTop();
	}

	private boolean isBelow(Widget from, Widget to) {
		return from.getAbsoluteTop() > to.getAbsoluteTop() + to.getOffsetHeight();
	}

	private boolean isRightOf(Widget from, Widget to) {
		return from.getAbsoluteLeft() - from.getOffsetWidth() / 2 > to.getAbsoluteLeft() + to.getOffsetWidth();
	}

	private boolean isHalfRightOf(Widget from, Widget to) {
		return from.getAbsoluteLeft() > (to.getAbsoluteLeft() + to.getOffsetWidth());
	}

	private boolean isLeftOf(Widget from, Widget to) {
		return from.getAbsoluteLeft() + from.getOffsetWidth() < to.getAbsoluteLeft() - to.getOffsetWidth() / 2;
	}

	private boolean isHalfLeftOf(Widget from, Widget to) {
		return from.getAbsoluteLeft() + from.getOffsetWidth() < to.getAbsoluteLeft();
	}

	private void lineFromRightToLeft(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth();
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() / 2;
		
		int endX = e.to.getAbsoluteLeft() - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo((startX + endX) / 2, startY, (startX + endX) / 2, endY, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY);
			ctx.stroke();
		}
		arrowHeadRight(endX, endY);
	}
	
	private void moveLabel(Label l, int startX, int startY, int endX, int endY) {
//		int x = (startX + endX) / 2;
//		int y = (startY + endY) / 2;
		int x = startX + (endX - startX) / 4;
		int y = startY + (endY - startY) / 4;
		setWidgetLeftWidth(l, x, Unit.PX, 3, Unit.EM);
		setWidgetTopHeight(l, y, Unit.PX, 1.5, Unit.EM);
	}

	private void lineFromLeftToRight(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft);
		int startY = (e.from.getAbsoluteTop() - absTop) + e.from.getOffsetHeight() / 2;
		
		int endX = e.to.getAbsoluteLeft() - absLeft + e.to.getOffsetWidth();;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo((startX + endX) / 2, startY, (startX + endX) / 2, endY, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadLeft(endX, endY);
	}
	
	private void lineFromTopToLeft(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() - absTop);
		
		int endX = e.to.getAbsoluteLeft() - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(startX, endY, startX, endY, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadRight(endX, endY);
	}
	
	private void lineFromBottomToTop(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() - absTop + e.from.getOffsetHeight());
		
		int endX = e.to.getAbsoluteLeft() - absLeft + e.to.getOffsetWidth() / 2;
		int endY = e.to.getAbsoluteTop() - absTop;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(startX, (startY + endY) / 2, endX, (startY + endY) / 2, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadDown(endX, endY);
	}
	
	private void lineFromTopToBottom(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() - absTop);
		
		int endX = e.to.getAbsoluteLeft() - absLeft + e.to.getOffsetWidth() / 2;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight();

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(startX, (startY + endY) / 2, endX, (startY + endY) / 2, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadUp(endX, endY);
	}
	
	private void lineFromTopToRight(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() - absTop);
		
		int endX = e.to.getAbsoluteLeft() + e.to.getOffsetWidth()- absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(startX, endY, startX, endY, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadLeft(endX, endY);
	}
	
	private void lineFromBottomToLeft(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() + e.from.getOffsetHeight() - absTop);
		
		int endX = e.to.getAbsoluteLeft() - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(startX, endY, startX, endY, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadRight(endX, endY);
	}
	
	private void lineFromBottomToRight(Context2d ctx, Edge e, int absLeft, int absTop) {
		int startX = (e.from.getAbsoluteLeft() - absLeft) + e.from.getOffsetWidth() / 2;
		int startY = (e.from.getAbsoluteTop() + e.from.getOffsetHeight() - absTop);
		
		int endX = e.to.getAbsoluteLeft() + e.to.getOffsetWidth() - absLeft;
		int endY = e.to.getAbsoluteTop() - absTop + e.to.getOffsetHeight() / 2;

		if (canvas != null) {
			ctx.beginPath();
			ctx.moveTo(startX, startY);
			ctx.bezierCurveTo(startX, endY, startX, endY, endX, endY);
//			ctx.fillText(e.text, (startX + endX) / 2, (startY + endY) / 2);
			moveLabel(e.l, startX, startY, endX, endY); 			
			ctx.stroke();
		}
		arrowHeadLeft(endX, endY);
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
		wrappers.put(widget, sr);
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

	@Override
	public void onResize() {
		super.onResize();
		updateCanvasSize();
		drawEdges();
	}

	
	

}

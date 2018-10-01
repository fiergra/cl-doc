package com.ceres.dynamicforms.client.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ceres.dynamicforms.client.ITranslator;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.Interactor.LinkChangeHandler;
import com.ceres.dynamicforms.client.InteractorLink;
import com.ceres.dynamicforms.client.PushButtonLink;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public abstract class MapListRenderer extends FlexTable implements HasEnabled {

	public static final String  LABELS = "labels";
	public static final String  WIDTHS = "widths";
	public static final String CLASSNAME = "className";
	

	static class LineContext {
//		private final int row;
		private final MapListRenderer mlr;
		public LineContext(MapListRenderer mlr, /*int row, */Map<String, Serializable> act) {
			this.mlr = mlr;
//			this.row = row;
			this.act = act;
		}

		final Interactor<Map<String, Serializable>> interactor = new Interactor<Map<String, Serializable>>() {

			@Override
			public void hilite(boolean isValid) {
				if (!isValid) {
					mlr.getRowFormatter().addStyleName(mlr.getRow(LineContext.this), "invalidContent");
				} else {
					mlr.getRowFormatter().removeStyleName(mlr.getRow(LineContext.this), "invalidContent");
				}
			}
			
		};
		final Map<String, Serializable> act;
	}
	
	private final List<LineContext> lineContexts = new ArrayList<LineContext>();
	private final List<Interactor<Map<String, Serializable>>> interactors = new ArrayList<>();
	private final List<Map<String, Serializable>> deleted = new ArrayList<Map<String,Serializable>>();

	
	private final String[] labels;
	private Runnable changeHandler;
	private LineContext lc;
	private ITranslator<Map<String, Serializable>> translator;

	public MapListRenderer(ITranslator<Map<String, Serializable>> translator, String[] labels, Runnable changeHandler) {
		this.translator = translator;
		this.labels = labels;
		setStyleName("namedValuesList");
		this.changeHandler = changeHandler;
		addHeader();
	}

	public void setChangeHandler(Runnable changeHandler) {
		this.changeHandler = changeHandler;
	}


	
	protected int getRow(LineContext lineContext) {
		return lineContexts.indexOf(lineContext) + 1 /* the header is row zero! */;
	}

	private LineContext emptyLineContext = null;
	private boolean enabled = true;
	
	private void addEmptyLine() {
		if (enabled && emptyLineContext == null) {
			final Map<String,Serializable> newAct = newAct();
			emptyLineContext = addRow(newAct);
		}
	}
	
	

	@Override
	public void removeRow(int row) {
		if (row < getRowCount()) {
			super.removeRow(row);
		} else {
			System.out.println(this + " row index out of bounds!");
		}
	}

	private void removeEmptyLine() {
		if (emptyLineContext != null) {
			int row = getRow(emptyLineContext);
			removeRow(row);
			lineContexts.remove(emptyLineContext);
			emptyLineContext = null;
		}
	}

	protected abstract Map<String, Serializable> newAct();
	
	protected abstract boolean isValid(Interactor<Map<String, Serializable>> interactor);
	
	public List<Interactor<Map<String, Serializable>>> getInteractors() {
		return interactors;
	}
	
	public boolean isModified() {
		boolean isModified = false;
		Iterator<LineContext> iter = lineContexts.iterator();
		while (!isModified && iter.hasNext()) {
			isModified = isModified || iter.next().interactor.isModified();
		}
		return isModified;
	}

	public void addRow(Map<String, Serializable> newAct, boolean isModified) {
		removeEmptyLine();
		lc = addRow(newAct);
		lc.interactor.setModified(isModified);
		emptyLineContext = null;
		addEmptyLine();
	}

	private LineContext addRow(Map<String, Serializable> newAct) {
		final LineContext lineContext = new LineContext(this, newAct);

		lineContext.interactor.addChangeHandler(new LinkChangeHandler<Map<String, Serializable>>() {
			
			@Override
			public void onChange(InteractorLink<Map<String, Serializable>> link) {
				if (changeHandler != null) {
					changeHandler.run();
				}
				
				if (lineContext.interactor.isValid() && isValid(lineContext.interactor) && isLastLine(lineContext)) {
					emptyLineContext = null;
					addEmptyLine();
				} else if (lineContexts.contains(lineContext) && lineContext.interactor.isEmpty() && !isLastLine(lineContext)) {
					if (canRemove(lineContext.act)) {
						removeRow(lineContext);
					}
				}
			}

		});

			
		lineContexts.add(lineContext);
		interactors.add(lineContext.interactor);
		
		final int row = getRowCount();
		createNewRow(row, lineContext.interactor);
		getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
		Image img = new Image("assets/images/workflow/actions/delete.png");
		img.setPixelSize(16, 16);
		
		PushButton pbDelete = new PushButton(img);
		pbDelete.setPixelSize(18, 18);
		pbDelete.setStyleName("mapListDeleteButton");
		final InteractorLink<Map<String, Serializable>> link = new PushButtonLink(lineContext.interactor, "$pbDelete", pbDelete, null);
		lineContext.interactor.addLink(link);
		lineContext.interactor.addChangeHandler(new LinkChangeHandler<Map<String, Serializable>>() {
			
			@Override
			protected void onChange(InteractorLink<Map<String, Serializable>> l) {
				if (l == link && !isLastLine(lineContext)) {
					removeRow(lineContext);
				}
			}
		});
		setWidget(row, getCellCount(row), pbDelete);
		lineContext.interactor.toDialog(translator, newAct);
		enableRow(row, enabled);
		
		return lineContext;
	}

	private void removeRow(LineContext lineContext) {
		lineContext.act.put("isDeleted", true);
		int row = getRow(lineContext);
		removeRow(row);
		lineContexts.remove(lineContext);
		interactors.remove(lineContext.interactor);
		deleted.add(lineContext.act);
		
		lineContexts.get(row-1).interactor.setFocus();
	}

	

	private void enableRow(int row, boolean enabled) {
		for (int col = 0; col < getCellCount(row); col++) {
			Widget w = getWidget(row, col);
			if (w instanceof HasEnabled) {
				((HasEnabled)w).setEnabled(enabled);
			}
		}
	}

	protected abstract boolean canRemove(Map<String, Serializable> act);

	protected abstract void createNewRow(int row, Interactor<Map<String, Serializable>> interactor);

	protected boolean isLastLine(LineContext interactor) {
		return lineContexts.indexOf(interactor) == lineContexts.size() - 1;
	}

	public void fromDialog(List<Map<String,Serializable>> acts) {
		for (int i = 0; i < lineContexts.size() - 1; i++) {
			LineContext lc =lineContexts.get(i);
			lc.interactor.fromDialog(lc.act);
			acts.add(lc.act);
		}
	}

	public void toDialog(List<Map<String,Serializable>> acts) {
		setActs(acts);
	}
	
	public List<Map<String,Serializable>> getActs() {
		List<Map<String,Serializable>> acts = new ArrayList<Map<String,Serializable>>(lineContexts.size());
		for (int i = 0; i < lineContexts.size() - 1; i++) {
			LineContext lc =lineContexts.get(i);
			acts.add(lc.act);
		}
		return acts;
	}

	public void setActs(List<Map<String,Serializable>> result) {
		lineContexts.clear();
		interactors.clear();
		deleted.clear();
		
		clear();
		removeEmptyLine();
		removeAllRows();
		addHeader();
		
		if (result != null) {
			for (Map<String,Serializable> act:result) {
				addRow(act);
			}
		}		
		addEmptyLine();
	}

	private void addHeader() {
		int col = 0;
		getRowFormatter().addStyleName(0, "actsListHeader");
		for (String labelText:labels) {
			Label label = new Label(labelText);
			label.addStyleName("noWrap");
			setWidget(0, col, label);
			col+= 1;
		}
	}

	public List<Map<String,Serializable>> getChangedObjects() {
		List<Map<String,Serializable>> result = new ArrayList<Map<String,Serializable>>();
		for (LineContext lc:lineContexts) {
			if (lc.interactor.isModified()) {
				lc.interactor.fromDialog(lc.act);
				result.add(lc.act);
			}
		}
		
		for (Map<String, Serializable> d:deleted) {
			result.add(d);
		}
		return result;
	}

	public void removeAct(Map<String,Serializable> act) {
		int row = 1;
		Iterator<LineContext> iter = lineContexts.iterator();
		while (iter.hasNext()) {
			if (iter.next().act.equals(act)) {
				removeRow(row);
				iter.remove();
			}
			row++;
		}

	}

	@Override
	public boolean isEnabled() {
		return this.enabled ;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (!enabled) {
			removeEmptyLine();
		} else {
			addEmptyLine();
		}

		for (int row = 0; row < getRowCount(); row++) {
			for (int col = 0; col < getCellCount(row); col++) {
				Widget w = getWidget(row, col);
				if (w instanceof HasEnabled) {
					((HasEnabled)w).setEnabled(enabled);
				}
			}
		}

	}

	
	
}

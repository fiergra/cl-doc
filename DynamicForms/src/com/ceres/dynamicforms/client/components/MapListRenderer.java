package com.ceres.dynamicforms.client.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ceres.dynamicforms.client.Interactor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public abstract class MapListRenderer extends FlexTable {
	
	static class LineContext {
		private final int row;
		private final MapListRenderer mlr;
		public LineContext(MapListRenderer mlr, int row, Map<String, Serializable> act) {
			this.mlr = mlr;
			this.row = row;
			this.act = act;
		}

		final Interactor interactor = new Interactor() {

			@Override
			public void hilite(boolean isValid) {
				if (!isValid) {
					mlr.getRowFormatter().addStyleName(row, "invalidContent");
				} else {
					mlr.getRowFormatter().removeStyleName(row, "invalidContent");
				}
			}
			
		};
		final Map<String, Serializable> act;
	}
	
	private final List<LineContext> lineContexts = new ArrayList<LineContext>();
	private final List<Interactor> interactors = new ArrayList<Interactor>();
	private final List<Map<String, Serializable>> deleted = new ArrayList<Map<String,Serializable>>();

	
	private final String[] labels;
	private final Runnable setModified;

	public MapListRenderer(String[] labels, Runnable setModified) {
		this.labels = labels;
		setStyleName("namedValuesList");
		this.setModified = setModified;
	}

	private void addEmptyLine() {
		final Map<String,Serializable> newAct = newAct();
		addRow(newAct);
	}

	protected abstract Map<String, Serializable> newAct();
	
	protected abstract boolean isValid(Interactor interactor);
	
	public List<Interactor> getInteractors() {
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
	
	private void addRow(Map<String, Serializable> newAct) {
		final int row = getRowCount();
		final LineContext lineContext = new LineContext(this, row, newAct);

		lineContext.interactor.addChangeHandler(new Runnable() {
			
			@Override
			public void run() {
				if (setModified != null) {
					setModified.run();
				}
				if (lineContext.interactor.isValid() && isValid(lineContext.interactor) && isLastLine(lineContext)) {
					addEmptyLine();
				} else if (lineContexts.contains(lineContext) && lineContext.interactor.isEmpty() && !isLastLine(lineContext)) {
					if (canRemove(lineContext.act)) {
						lineContext.act.put("isDeleted", true);
						removeRow(row);
						lineContexts.remove(lineContext);
						interactors.remove(lineContext.interactor);
						
						deleted.add(lineContext.act);
					}
				}
			}
		});
		
		lineContexts.add(lineContext);
		interactors.add(lineContext.interactor);
		
		createNewRow(row, lineContext.interactor);
		lineContext.interactor.toDialog(newAct);
		
	}


	protected abstract boolean canRemove(Map<String, Serializable> act);

	protected abstract void createNewRow(int row, Interactor interactor);

	protected boolean isLastLine(LineContext interactor) {
		return lineContexts.indexOf(interactor) == lineContexts.size() - 1;
	}

	public List<Map<String,Serializable>> getActs() {
		List<Map<String,Serializable>> acts = new ArrayList<Map<String,Serializable>>(lineContexts.size() - 1);
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

}

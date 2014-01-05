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
		public LineContext(Map<String, Serializable> act) {
			this.act = act;
		}

		final Interactor interactor = new Interactor();
		final Map<String, Serializable> act;
	}
	
	private final List<LineContext> lineContexts = new ArrayList<LineContext>();
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
	
	protected abstract boolean isValid(List<Interactor>interactors, Interactor interactor);
	
	public boolean isModified() {
		boolean isModified = false;
		Iterator<LineContext> iter = lineContexts.iterator();
		while (!isModified && iter.hasNext()) {
			isModified = isModified || iter.next().interactor.isModified();
		}
		return isModified;
	}
	
	private void addRow(Map<String, Serializable> newAct) {
		final LineContext lineContext = new LineContext(newAct);
		final int row = getRowCount();

		lineContext.interactor.setChangeHandler(new Runnable() {
			
			@Override
			public void run() {
				if (setModified != null) {
					setModified.run();
				}
				if (lineContext.interactor.isValid() && isLastLine(lineContext) && lineContext.interactor.isValid()) {
					addEmptyLine();
				} else if (lineContext.interactor.isEmpty() && !isLastLine(lineContext)) {
					if (canRemove(lineContext.act)) {
						removeRow(row);
						lineContexts.remove(lineContext);
					}
				}
			}
		});
		
		lineContexts.add(lineContext);
		createNewRow(row, lineContext.interactor);
		lineContext.interactor.toDialog(newAct);
	}


	protected abstract boolean canRemove(Map<String, Serializable> act);

	protected abstract void createNewRow(int row, Interactor interactor);

	protected boolean isLastLine(LineContext interactor) {
		return lineContexts.indexOf(interactor) == lineContexts.size() - 1;
	}

	public void setActs(List<Map<String,Serializable>> result) {
		lineContexts.clear();
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
//			getFlexCellFormatter().setColSpan(0, col, 1);
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

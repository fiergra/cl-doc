package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.dynamicforms.client.INamedValues;
import com.ceres.dynamicforms.client.Interactor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public abstract class NamedValuesListRenderer extends FlexTable {
	
	static class LineContext {
		public LineContext(INamedValues act) {
			this.act = act;
		}

		final Interactor interactor = new Interactor();
		final INamedValues act;
	}
	
	private final ClDoc clDoc;
	
	private final List<LineContext> lineContexts = new ArrayList<LineContext>();
	private final String xmlLayout;

	private final String[] labels;

	public NamedValuesListRenderer(final ClDoc clDoc, String[] labels, String xmlLayout, Runnable setModified) {
		this.clDoc = clDoc;
		this.labels = labels;
		this.xmlLayout = xmlLayout;
		setStyleName("namedValuesList");
	}

	private void addEmptyLine() {
		final INamedValues newAct = newAct();
		addRow(newAct);
	}

	abstract INamedValues newAct();
	
	abstract boolean isValid(List<Interactor>interactors, Interactor interactor);
	
	private void addRow(INamedValues newAct) {
		final LineContext lineContext = new LineContext(newAct);
		final int row = getRowCount();

		lineContext.interactor.setChangeHandler(new Runnable() {
			
			@Override
			public void run() {
				if (lineContext.interactor.isValid() && isLastLine(lineContext) && lineContext.interactor.isValid()) {
					addEmptyLine();
				} else if (lineContext.interactor.isEmpty() && !isLastLine(lineContext)) {
					if (canRemove(lineContext.act)) {
						removeRow(row);
					}
				}
			}
		});
		
		lineContexts.add(lineContext);
		createNewRow(row, lineContext.interactor);
		lineContext.interactor.toDialog(newAct);
	}


	protected abstract boolean canRemove(INamedValues act);

	protected abstract void createNewRow(int row, Interactor interactor);

	protected boolean isLastLine(LineContext interactor) {
		return lineContexts.indexOf(interactor) == lineContexts.size() - 1;
	}

	public void setActs(List<INamedValues> result) {
		lineContexts.clear();
		clear();
		removeAllRows();
		addHeader();
		
		if (result != null) {
			for (INamedValues act:result) {
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

	public List<INamedValues> getChangedObjects() {
		List<INamedValues> result = new ArrayList<INamedValues>();
		for (LineContext lc:lineContexts) {
			if (lc.interactor.isModified()) {
				lc.interactor.fromDialog(lc.act);
				result.add(lc.act);
			}
		}
		return result;
	}

	public void removeAct(INamedValues act) {
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

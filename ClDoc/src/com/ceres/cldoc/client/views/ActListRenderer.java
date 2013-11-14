package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.views.Form.DataType;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.IAct;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class ActListRenderer extends DockLayoutPanel implements IView {
	
	private final FlexTable actsPanel = new FlexTable();
	private final List<FieldDef> fieldDefs;
	private final ClDoc clDoc;
	
	private final List<ClInteractor> interactors = new ArrayList<ClInteractor>();
	private Date date;
	private final Runnable setModified;

	public static class FieldDef {
		public FieldDef(String label, DataType type, boolean isRequired, HashMap<String, String> attributes) {
			this.label = label;
			this.type = type;
			this.isRequired = isRequired;
			this.attributes = attributes;
		}

		public String label;
		public Form.DataType type;
		public boolean isRequired;
		public HashMap<String, String> attributes;
		
	}
	
	public ActListRenderer(final ClDoc clDoc, List<FieldDef> fieldDefs, Date date, Runnable setModified) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.fieldDefs = fieldDefs;
		this.date = date;
		this.setModified = setModified;
		
		actsPanel.setWidth("100%");
		add(actsPanel);
	}

	private void addEmptyLine() {
		final Act newAct = newAct();
		addRow(newAct);
	}

	abstract Act newAct();
	
	abstract boolean isValid(List<ClInteractor>interactors, ClInteractor interactor);
	
	private void addRow(Act newAct) {
//		final Label lbLineStatus = new Label("-");
//		final Label lbLineModified = new Label("-");
		final ClInteractor interactor = new ClInteractor(clDoc.getSession(), newAct, null, null);
		final int row = actsPanel.getRowCount();
		
		interactor.setModificationCallback(new Runnable() {
			
			@Override
			public void run() {
//				lbLineModified.setText("*"); 
				setModified.run();
			}
		});
		interactor.setAnyModificationCallback(new Runnable() {
			
			@Override
			public void run() {
				if (interactor.isEmpty() && !isLastLine(interactor)) {
//					actsPanel.remove(hp);
					actsPanel.removeRow(row);
					interactor.setDeleted(true);
				}
			}
		});
		interactor.setValidationCallback(new ValidationCallback() {
			
			@Override
			public void setValid(ClInteractorLink link, boolean isValid) {
//				lbLineStatus.setText(interactor.isValid() ? "ok" : "!");
				if (isLastLine(interactor) && interactor.isValid() && isValid(interactors, interactor)) {
					addEmptyLine();
				}
			}

		});
		interactors.add(interactor);
//		actsPanel.setWidget(row, col++, lbLineModified);
//		actsPanel.setWidget(row, col++, lbLineStatus);
		int index = 0;
		Iterator<FieldDef> iter = fieldDefs.iterator();
		int col = 0;

		while (iter.hasNext()) {
			FieldDef dt = iter.next();
			final String fieldName = dt.label;
			final Widget widget = interactor.createWidgetForType(clDoc, dt.type, dt.isRequired, dt.attributes);
//			final Label lbStatus = new Label();
			ValidationStatus vs = new ValidationStatus(){

				@Override
				public void set(States state) {
//					lbStatus.setText(state.toString());
				}
				
			};
//			actsPanel.setWidget(row, col++, lbStatus);
			final ClInteractorLink link = new ClInteractorLink(interactor, vs, fieldName, widget, dt.type, dt.isRequired, dt.attributes);
			interactor.addLink(fieldName, link);
			actsPanel.setWidget(row, col, widget);
			if (!iter.hasNext()) {
				actsPanel.getColumnFormatter().setWidth(col, "100%");
				widget.setWidth("100%");
			}
			col += 1;
		}
		interactor.toDialog();
	}


	protected boolean isLastLine(ClInteractor interactor) {
		return interactors.indexOf(interactor) == interactors.size() - 1;
	}

	public void setActs(List<Act> result, Date date) {
		this.date = date;
		interactors.clear();
		actsPanel.clear();
		actsPanel.removeAllRows();
		addHeader();
		
		for (Act act:result) {
			addRow(act);
		}
		
		addEmptyLine();
	}

	private void addHeader() {
		int col = 0;
		actsPanel.getRowFormatter().addStyleName(0, "actsListHeader");
		for (FieldDef fd:fieldDefs) {
			Label label = new Label(fd.label);
			actsPanel.setWidget(0, col, label);
			actsPanel.getFlexCellFormatter().setColSpan(0, col, 1);
			col+= 1;
		}
	}

	@Override
	public IAct getModel() {
		return null;
	}

	@Override
	public void fromDialog() {
		for (ClInteractor i:interactors) {
			i.fromDialog();
		}
	}

	@Override
	public void toDialog() {
		for (ClInteractor i:interactors) {
			i.toDialog();
		}
		clearModification();
	}

	@Override
	public boolean isModified() {
		boolean isModified = false;
		for (ClInteractor i:interactors) {
			isModified |= i.isModified();
		}
		return isModified;
	}

	@Override
	public void clearModification() {
		for (ClInteractor i:interactors) {
			i.clearModification();
		}
//		setModified(false);
	}
	
	protected List<Act> getModifiedActs() {
		List<Act> acts = new ArrayList<Act>();
		for (ClInteractor interactor:interactors) {
			if (interactor.isModified() && (interactor.isValid() || interactor.isDeleted())) {
				interactor.fromDialog();
				Act act = (Act) interactor.getModel();
				act.isDeleted = interactor.isDeleted();
				acts.add(act);
			}
		}
		return acts;
		
	}
	
}

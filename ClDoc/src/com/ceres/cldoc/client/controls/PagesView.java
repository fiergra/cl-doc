package com.ceres.cldoc.client.controls;

import java.util.ArrayList;
import java.util.List;

import com.ceres.cldoc.client.views.IView;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class PagesView<T> extends TabLayoutPanel implements IView<T> {

	private final T model;
	private final List<IView<T>> pages = new ArrayList<IView<T>>();

	public PagesView(T model) {
		super(2, Unit.EM);
		this.model = model;
	}

	@Override
	public T getModel() {
		return model;
	}

	public void addPage(IView<T> page, String label) {
		pages.add(page);
		DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
		dp.add(page);
		add(dp, label);
	}
	
	@Override
	public void fromDialog() {
		for (IView<T> page:pages) {
			page.fromDialog();
		}
	}		

	@Override
	public void toDialog() {
		for (IView<T> page:pages) {
			page.toDialog();
		}
	}

	@Override
	public boolean isModified() {
		boolean isModified = false;

		for (IView<T> page:pages) {
			isModified |= page.isModified();
		}
		
		return isModified;
	}

	@Override
	public void clearModification() {
		for (IView<T> page:pages) {
			page.clearModification();
		}
		
	}

}

package com.ceres.cldoc.client.controls;

import java.util.ArrayList;
import java.util.List;

import com.ceres.cldoc.client.views.IForm;
import com.ceres.cldoc.model.IAct;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class PagesView extends TabLayoutPanel implements IForm {

	private final IAct model;
	private final List<IForm> pages = new ArrayList<IForm>();

	public PagesView(IAct model) {
		super(2, Unit.EM);
		this.model = model;
	}

	@Override
	public IAct getModel() {
		return model;
	}

	public void addPage(IForm page, String label) {
		pages.add(page);
		DockLayoutPanel dp = new DockLayoutPanel(Unit.EM);
		dp.add(page);
		add(dp, label);
	}
	
	@Override
	public void fromDialog() {
		for (IForm page:pages) {
			page.fromDialog();
		}
	}		

	@Override
	public void toDialog() {
		for (IForm page:pages) {
			page.toDialog();
		}
	}

	@Override
	public boolean isModified() {
		boolean isModified = false;

		for (IForm page:pages) {
			isModified |= page.isModified();
		}
		
		return isModified;
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;

		for (IForm page:pages) {
			isValid &= page.isValid();
		}
		
		return isValid;
	}

	@Override
	public void clearModification() {
		for (IForm page:pages) {
			page.clearModification();
		}
		
	}

}

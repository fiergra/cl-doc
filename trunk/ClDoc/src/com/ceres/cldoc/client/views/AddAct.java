package com.ceres.cldoc.client.views;

import java.util.Iterator;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.core.IEntity;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class AddAct extends DialogBox {

	private final OnOkHandler<Act> onOk;
	private final ClDoc clDoc;

	public AddAct(ClDoc clDoc, IEntity humanBeing, List<Act> acts, OnOkHandler<Act> onOk) {
		this.clDoc = clDoc;
		this.onOk = onOk;
		setup(humanBeing, acts);
	}

//	HorizontalPanel hp = new HorizontalPanel();
//	hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//	Button addNew = new Button("Act");
//	hp.setSpacing(8);
//	hp.add(new Label("add"));
//	hp.add(addNew);
//
	
	private void setup(final IEntity entity, final List<Act> acts) {
		setText(SRV.c.add());
		DockLayoutPanel widget = new DockLayoutPanel(Unit.PX);
		final ListBox list = new ListBox();
		list.setSize("100%", "100%");
		HorizontalPanel filter = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		
		widget.setPixelSize(400, 250);
		filter.add(new Label(SRV.c.type()));
		final TextBox filterText = new TextBox();
		filter.add(filterText);
		filterText.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				populateList(filterText.getText(), entity.getType(), list, acts);
			}
		});
		filter.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		filter.setSpacing(5);
		
		list.setVisibleItemCount(Integer.MAX_VALUE);
		list.addDoubleClickHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				onOk(list);
			}
		});
		populateList(null, entity.getType(), list, acts);
		
		Button pbOk = new Button(SRV.c.ok());
		pbOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onOk(list);
			}
		});
		
		
		final Button pbCancel = new Button(SRV.c.cancel());

		buttons.add(pbOk);
		buttons.add(pbCancel);
		pbCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});
		
		buttons.add(pbCancel);
		
		widget.addNorth(filter, 32);
		widget.addSouth(buttons, 32);
		
		widget.add(list);
		setWidget(widget);
	}

	protected void onOk(ListBox list) {
		int index = list.getSelectedIndex();
		String selection = list.getValue(index);
		SRV.configurationService.getLayoutDefinition(clDoc.getSession(), selection, LayoutDefinition.FORM_LAYOUT, new DefaultCallback<LayoutDefinition>(clDoc, "getLayoutDef") {

			@Override
			public void onResult(LayoutDefinition result) {
				Act vb = new Act(result.actClass);
				onOk.onOk(vb);
				close();
			}
		});
	}

	protected void close() {
		hide();
	}

	private void populateList(String filter, long entityType, final ListBox list, final List<Act> acts) {
		SRV.configurationService.listLayoutDefinitions(clDoc.getSession(), LayoutDefinition.FORM_LAYOUT, entityType, null, new DefaultCallback<List<LayoutDefinition>>(clDoc, "getLayoutDefs") {

			@Override
			public void onResult(List<LayoutDefinition> result) {
				list.clear();
				int row = 0;
				
				for (LayoutDefinition layoutDef : result) {
					if (!layoutDef.actClass.isSingleton || !alreadyThere(layoutDef.actClass, acts)) {
						list.addItem(layoutDef.actClass.name);
					}
//					list.setWidget(row++, 0, new Label(fds.name));
				}
			}

			private boolean alreadyThere(ActClass actClass, List<Act> acts) {
				Iterator<Act> iter = acts.iterator();
				boolean found = false;
				
				while (!found && iter.hasNext()) {
					found = iter.next().actClass.name.equals(actClass.name);
				}
				return found;
			}
		});
	}
	
	public static void addAct(ClDoc clDoc, IEntity humanBeing, List<Act> acts, OnOkHandler<Act> onOk) {
		AddAct avb = new AddAct(clDoc, humanBeing, acts, onOk);
		avb.setGlassEnabled(true);
		avb.setAnimationEnabled(true);
		avb.center();
	}

}

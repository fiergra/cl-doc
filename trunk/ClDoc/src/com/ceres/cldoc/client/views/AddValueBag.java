package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.FormClassDesc;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.HumanBeing;
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
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddValueBag extends DialogBox {

	private OnOkHandler<GenericItem> onOk;

	public AddValueBag(HumanBeing humanBeing, OnOkHandler<GenericItem> onOk) {
		this.onOk = onOk;
		setup(humanBeing);
	}

//	HorizontalPanel hp = new HorizontalPanel();
//	hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//	Button addNew = new Button("ValueBag");
//	hp.setSpacing(8);
//	hp.add(new Label("add"));
//	hp.add(addNew);
//
	
	private void setup(HumanBeing humanBeing) {
		setText("Add");
		DockLayoutPanel widget = new DockLayoutPanel(Unit.PX);
		final ListBox list = new ListBox();
		HorizontalPanel filter = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		
		widget.setPixelSize(400, 550);
		filter.add(new Label("type"));
		final TextBox filterText = new TextBox();
		filter.add(filterText);
		filterText.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				populateList(filterText.getText(), list);
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
		populateList(null, list);
		
		Button pbOk = new Button("Ok");
		pbOk.setStylePrimaryName("button");
		pbOk.addStyleName("gray");
		pbOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onOk(list);
			}
		});
		
		
		final Button pbCancel = new Button("Cancel");

		buttons.add(pbOk);
		buttons.add(pbCancel);
		
		pbCancel.setStylePrimaryName("button");
		pbCancel.addStyleName("gray");
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
		SRV.configurationService.getFormClassDesc(selection, new DefaultCallback<FormClassDesc>() {

			@Override
			public void onSuccess(FormClassDesc result) {
				GenericItem vb = new GenericItem(result.name, result.xmlLayout);
				onOk.onOk(vb);
				close();
			}
		});
	}

	protected void close() {
		hide();
	}

	private void populateList(String filter, final ListBox list) {
		SRV.configurationService.listClasses(null, filter, new DefaultCallback<List<FormClassDesc>>() {

			@Override
			public void onSuccess(List<FormClassDesc> result) {
				list.clear();
				int row = 0;
				
				for (FormClassDesc fds : result) {
					list.addItem(fds.name);
//					list.setWidget(row++, 0, new Label(fds.name));
				}
			}
		});
	}
	
	public static void addValueBag(HumanBeing humanBeing, OnOkHandler<GenericItem> onOk) {
		AddValueBag avb = new AddValueBag(humanBeing, onOk);
		avb.setGlassEnabled(true);
		avb.setAnimationEnabled(true);
		avb.center();
	}

}

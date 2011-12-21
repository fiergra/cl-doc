package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class UploadDialog extends DialogBox {

	private OnOkHandler<Void> onOk;

	public UploadDialog(HumanBeing humanBeing, OnOkHandler<Void> onOk) {
		this.onOk = onOk;
		setup(humanBeing);
	}

	private FormPanel createUploadPanel(HumanBeing humanBeing) {
		final FormPanel form = new FormPanel();
		final FileUpload fup = new FileUpload();
		fup.setName("fup");
	
		final TextBox rweKey = new TextBox();
		rweKey.setName("rweKey");
		rweKey.setVisible(false);
		rweKey.setText(humanBeing.id.toString());
	
		final TextBox fileName = new TextBox();
		fileName.setName("fileName");
		fileName.setVisible(false);
	
		final HorizontalPanel formWidget = new HorizontalPanel();
		form.setWidget(formWidget);
		
		formWidget.add(rweKey);
		formWidget.add(fileName);
		formWidget.add(fup);
	
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		String uploadUrl = null;
		form.setAction(uploadUrl);
	
		form.addSubmitHandler(new SubmitHandler() {
			
			@Override
			public void onSubmit(SubmitEvent event) {
				fileName.setText(fup.getFilename());
				System.out.println("do submit..." + fup.getFilename());
				System.out.println(event);
			}
		});
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
	
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				close();
				onOk.onOk(null);
			}
		});
	
		
		return form;
	}
	
	
	private void setup(HumanBeing humanBeing) {
		setText("Add");
		DockLayoutPanel widget = new DockLayoutPanel(Unit.PX);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		widget.setPixelSize(500, 250);
		
		Button pbOk = new Button("Ok");
		
		final FormPanel form = createUploadPanel(humanBeing);
		
		pbOk.setStylePrimaryName("button");
		pbOk.addStyleName("gray");
		pbOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SRV.configurationService
				.getUploadUrl(new DefaultCallback<String>() {

					@Override
					public void onSuccess(String result) {
						form.setAction(result);
						form.submit();
					}
				});
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
		widget.addSouth(buttons, 32);
		widget.add(form);
		setWidget(widget);
	}

	protected void close() {
		hide();
	}

	public static void uploadFile(HumanBeing humanBeing, OnOkHandler<Void> onOk) {
		UploadDialog avb = new UploadDialog(humanBeing, onOk);
		avb.setGlassEnabled(true);
		avb.setAnimationEnabled(true);
		avb.center();
	}

}

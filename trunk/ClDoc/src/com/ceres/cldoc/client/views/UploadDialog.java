package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UploadDialog extends DialogBox {

	private final OnOkHandler<Void> onOk;

	public UploadDialog(ClDoc clDoc, String type, Entity humanBeing, Act act, OnOkHandler<Void> onOk) {
		this.onOk = onOk;
		setup(clDoc, type, humanBeing, act);
	}

	private FormPanel createUploadPanel(ClDoc clDoc, String type, Entity humanBeing, Act act) {
		final FormPanel form = new FormPanel();
		final FileUpload fup = new FileUpload();
		fup.setName("fup");
		
		final TextBox fileName = new TextBox();
		fileName.setName("fileName");
		fileName.setVisible(false);
	
		final TextArea comment = new TextArea();
		comment.setName("comment");
		comment.setWidth("100%");
		
		final VerticalPanel formWidget = new VerticalPanel();
		formWidget.setWidth("100%");
		form.setWidget(formWidget);

		String baseUrl = GWT.getModuleBaseURL();
		form.setAction(baseUrl + "uploadService?type="+type+"&sid=" + clDoc.getSession().getId() + "&uid=" + clDoc.getSession().getUser().id);

		if (humanBeing != null) {
			formWidget.add(createIdField("entityId", humanBeing.id));
		}
		if (act != null) {
			formWidget.add(createIdField("actId", act.id));
		}
		
		formWidget.add(fileName);
		formWidget.add(fup);
		formWidget.add(new Label(SRV.c.comment()));
		formWidget.add(comment);
	
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
	
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
				String text = event.getResults();
				new MessageBox("Upload", text != null && text.length() > 0 ? text : "Datei erfolgreich hochgeladen.", MessageBox.MB_OK, MessageBox.MESSAGE_ICONS.MB_ICON_INFO){

					@Override
					protected void onClick(int result) {
						close();
						onOk.onOk(null);
					}};
			}
		});
	
		return form;
	}
	
	
	private Widget createIdField(String name, Long id) {
		final TextBox rweKey = new TextBox();
		rweKey.setName(name);
		rweKey.setVisible(false);
		rweKey.setText(id.toString());
		return rweKey;
	}

	private void setup(ClDoc clDoc, String type, Entity humanBeing, Act act) {
		setText(SRV.c.add());
		DockLayoutPanel widget = new DockLayoutPanel(Unit.PX);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(5);
		widget.setPixelSize(500, 250);
		
		final Button pbOk = new Button(SRV.c.ok());
		final FormPanel form = createUploadPanel(clDoc, type, humanBeing, act);
		
		pbOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				pbOk.setEnabled(false);
				form.submit();
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
		widget.addSouth(buttons, 32);
		widget.add(form);
		setWidget(widget);
	}

	protected void close() {
		hide();
	}

	public static void uploadExternalDoc(ClDoc clDoc, Entity humanBeing, OnOkHandler<Void> onOk) {
		uploadFile(clDoc, ActClass.EXTERNAL_DOC.name, humanBeing, null, onOk);
	}

	public static void uploadExternalDoc(ClDoc clDoc, Act act, OnOkHandler<Void> onOk) {
		uploadFile(clDoc, ActClass.EXTERNAL_DOC.name, null, act, onOk);
	}

	public static void uploadLayouts(ClDoc clDoc, int layoutType, OnOkHandler<Void> onOk) {
		uploadFile(clDoc, layoutType == LayoutDefinition.FORM_LAYOUT ? "form_layouts" : "print_layouts", null, null, onOk);
	}

	public static void uploadCatalogs(ClDoc clDoc, OnOkHandler<Void> onOk) {
		uploadFile(clDoc, "catalogs", null, null, onOk);
	}

	private static void uploadFile(ClDoc clDoc, String type, Entity humanBeing, Act act, OnOkHandler<Void> onOk) {
		UploadDialog avb = new UploadDialog(clDoc, type, humanBeing, act, onOk);
		avb.setGlassEnabled(true);
		avb.setAnimationEnabled(true);
		avb.center();
	}

}

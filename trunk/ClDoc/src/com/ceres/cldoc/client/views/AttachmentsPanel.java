package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Attachment;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class AttachmentsPanel extends DockLayoutPanel {

	private ClickableTable<Attachment> attachments = null;
	
	public AttachmentsPanel(final ClDoc clDoc, final Act act) {
		super(Unit.EM);
		
		attachments = 
		new ClickableTable<Attachment>(clDoc,
				new ListRetrievalService<Attachment>() {

					@Override
					public void retrieve(String filter, AsyncCallback<List<Attachment>> callback) {
						SRV.actService.listAttachments(clDoc.getSession(), act, callback);
					}
				},
				new OnClick<Attachment>() {

					@Override
					public void onClick(Attachment attachment) {
						String baseUrl = GWT.getModuleBaseURL();
						Window.open(baseUrl + "download?id=" + attachment.docId , "_blank", "");
					}
				}, false)
				{

					@Override
					public boolean addRow(FlexTable table, int row, final Attachment attachment) {
						int col = 0;
						Image img = getMimeTypeImage(attachment.filename);
//						img.addClickHandler(new ClickHandler() {
//							
//							@Override
//							public void onClick(ClickEvent event) {
//								String baseUrl = GWT.getModuleBaseURL();
//								Window.open(baseUrl + "download?id=" + attachment.docId , "_blank", "");
//							}
//						});
						table.setWidget(row, col++, img);
						table.setWidget(row, col++, new HTML("<b>" + attachment.filename + "</b>"));
						table.setWidget(row, col++, new HTML("<i>" + attachment.description + "</i>"));
						img = new Image("icons/16/File-Delete-icon.png");
						img.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								event.stopPropagation();
								new MessageBox("Loeschen", "Wollen Sie den Anhang entgueltig loeschen?", MessageBox.MB_YES | MessageBox.MB_NO, MessageBox.MESSAGE_ICONS.MB_ICON_QUESTION){

									@Override
									protected void onClick(int result) {
										if (result == MessageBox.MB_YES) {
											SRV.actService.deleteAttachment(clDoc.getSession(), attachment, new DefaultCallback<Void>(clDoc, "delete") {
					
												@Override
												public void onSuccess(Void result) {
													attachments.refresh();
												}
											});
										}
									}};
							}
						});
						table.setWidget(row, col++, img);
						return true;
					}

					private Image getMimeTypeImage(String filename) {
						Image img;
						
						if (filename.toLowerCase().endsWith(".pdf")) {
							img = new Image("icons/16/Adobe-PDF-Document-icon.png");
						} else {
							img = new Image("icons/16/Document-icon.png");
						}
						return img;
					}
				};
		setPixelSize(500, 300);
		
		attachments.addButton(SRV.c.addAttachment(), "icons/32/Button-Upload-icon.png", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UploadDialog.uploadExternalDoc(clDoc, act,
						new OnOkHandler<Void>() {

							@Override
							public void onOk(Void result) {
								attachments.refresh();
							}
						});
			}
		});
		attachments.getColumnFormatter().addStyleName(2, "hundertPercentWidth");
		add(attachments);
		attachments.refresh();
	}

}

package com.ceres.cldoc.client.views;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Person;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class HistoryView extends DockLayoutPanel {

	private ActRenderer viewer;
	private ClickableTable<Act>historyPanel;
	
	private Person humanBeing;
	private ClDoc clDoc;

	private HashMap<String, LayoutDefinition> layouts = new HashMap<String, LayoutDefinition>();

	public HistoryView(final ClDoc clDoc, final Person model) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.humanBeing = model;
		historyPanel = new ClickableTable<Act>(clDoc, new ListRetrievalService<Act>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Act>> callback) {
				SRV.actService.findByEntity(clDoc.getSession(), humanBeing, callback);
			}
		}, new OnClick<Act>() {

			@Override
			public void onClick(final Act act) {
				setSelectedAct(act);
			}
		}, true){

			@Override
			public void addRow(FlexTable table, int row, Act act) {
				int column = 0;
				String imgSource = "externalDoc".equals(act.className) ? 
						"icons/16/Adobe-PDF-Document-icon.png" : "icons/16/Document-icon.png";
				table.setWidget(row, column++, new Image(imgSource));
				String sDate = act.date != null ? DateTimeFormat.getFormat("dd.MM.yyyy").format(act.date) : "--.--.----";
				table.setWidget(row, column++, new Label(sDate));
				table.setWidget(row, column++, new HTML("<b>" + act.className + "</b>"));
			}};

		Image pbUpload = historyPanel.addButton("upload file", "icons/32/Button-Upload-icon.png");
		Image pbAdd = historyPanel.addButton("add act", "icons/32/File-New-icon.png");

		pbUpload.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UploadDialog.uploadFile(HistoryView.this.clDoc, model,
						new OnOkHandler<Void>() {

							@Override
							public void onOk(Void result) {
								historyPanel.refresh();
							}
						});
			}
		});

		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.addWest(historyPanel, 400);

		viewer = new ActRenderer(clDoc, new OnOkHandler<Act>() {

			@Override
			public void onOk(Act result) {
				refresh(result);
			}
		}, new Runnable() {

			@Override
			public void run() {

			}
		});
		viewer.addStyleName("viewer");
		splitPanel.add(viewer);

		add(splitPanel);

		pbAdd.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddAct.addAct(clDoc, humanBeing, new OnOkHandler<Act>() {

					@Override
					public void onOk(Act act) {
						act.addParticipant(model, Catalog.PATIENT, new Date(), null);

						SRV.actService.save(clDoc.getSession(), act,
								new DefaultCallback<Act>(clDoc, "save") {

									@Override
									public void onSuccess(Act act) {
										refresh(act);
										setSelectedAct(act);
									}

								});

					}
				});
			}
		});
		refresh(null);
	}

	protected void refresh(Act act) {
		historyPanel.refresh();
		historyPanel.setSelected(act);
		setSelectedAct(act);
	}

	protected void setSelectedAct(final Act act) {
		if (act != null) {
			LayoutDefinition ld = layouts.get(act.className);
			if (ld == null) {
				SRV.configurationService.getLayoutDefinition(clDoc.getSession(), act.className, LayoutDefinition.FORM_LAYOUT, 
						new DefaultCallback<LayoutDefinition>(clDoc, "getLayoutDef") {

							@Override
							public void onSuccess(LayoutDefinition ld) {
								layouts.put(act.className, ld);
								if (viewer.setAct(ld, act)) {
								}
							}

						});
			} else {
				if (viewer.setAct(ld, act)) {
				}
			}
		}
	}
	
	
//	private void refresh(final Act act) {
//		SRV.actService.findByEntity(clDoc.getSession(), humanBeing,
//				new DefaultCallback<List<Act>>(clDoc, "findByEntity") {
//
//					@Override
//					public void onSuccess(List<Act> result) {
//						refresh(result, act);
//					}
//				});
//	}
//
//	private void refresh(List<Act> result, Act act) {
//		Act selected = ((SingleSelectionModel<Act>) historyList
//				.getSelectionModel()).getSelectedObject();
//		if (result != null) {
//			historyList.setRowCount(result.size());
//			historyList.setRowData(result);
//
//			if (act != null) {
//				historyList.getSelectionModel().setSelected(act, true);
//			} else if (selected != null) {
//				historyList.getSelectionModel().setSelected(selected, true);
//			}
//		}
//	}
}

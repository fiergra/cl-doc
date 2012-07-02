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
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
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

	private final ActRenderer viewer;
	private final ClickableTable<Act>historyPanel;
	
	private Entity e;
	private final ClDoc clDoc;

	private final HashMap<String, LayoutDefinition> layouts = new HashMap<String, LayoutDefinition>();

	public HistoryView(final ClDoc clDoc, Entity entity) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.e = entity;
		historyPanel = new ClickableTable<Act>(clDoc, new ListRetrievalService<Act>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Act>> callback) {
				SRV.actService.findByEntity(clDoc.getSession(), e, Participation.PATIENT,  callback);
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

		historyPanel.getColumnFormatter().addStyleName(2, "hundertPercentWidth");
		Image pbUpload = historyPanel.addButton("upload file", "icons/32/Button-Upload-icon.png", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UploadDialog.uploadExternalDoc(HistoryView.this.clDoc, e,
						new OnOkHandler<Void>() {

							@Override
							public void onOk(Void result) {
								historyPanel.refresh();
							}
						});
			}
		});
		Image pbAdd = historyPanel.addButton("add act", "icons/32/File-New-icon.png", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddAct.addAct(clDoc, e, new OnOkHandler<Act>() {

					@Override
					public void onOk(Act act) {
						act.addParticipant(e, Catalog.PATIENT, new Date(), null);
						act.addParticipant(clDoc.getSession().getUser().organisation, Catalog.ORGANISATION, new Date(), null);
						
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

		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		historyPanel.addStyleName("roundCorners");
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

		if (entity != null) {
			refresh(null);
		}
	}
	
	public void setModel(Entity entity) {
		e = entity;
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
//								Locator.getLogService().log(clDoc.getSession(), ILogService.VIEW, act, "");
								if (viewer.setAct(ld, act)) {
								}
							}

						});
			} else {
				if (viewer.setAct(ld, act)) {
				}
			}
		} else {
			viewer.setAct(null, null);
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

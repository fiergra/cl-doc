package com.ceres.cldoc.client.views;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTable;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class HistoryView extends DockLayoutPanel {

	private final ActRenderer viewer;
	private final ClickableTable<Act>historyPanel;
	
	private Entity e;
	private final ClDoc clDoc;
	private final TabLayoutPanel tab;
	private final HashMap<String, LayoutDefinition> layouts = new HashMap<String, LayoutDefinition>();

	public HistoryView(final ClDoc clDoc, Entity entity, final TabLayoutPanel tab) {
		super(Unit.EM);
		this.clDoc = clDoc;
		this.e = entity;
		this.tab = tab;
		final ListBox cmbFilter = new ListBox();
//		cmbFilter.setVisibleItemCount(1);
		
		historyPanel = new ClickableTable<Act>(clDoc, new ListRetrievalService<Act>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<Act>> callback) {
				SRV.actService.findByEntity(clDoc.getSession(), e, Participation.PROTAGONIST.id, callback);
			}
		}, new OnClick<Act>() {

			
			@Override
			public void onClick(final Act act) {
				SRV.actService.findById(clDoc.getSession(), act.id, new DefaultCallback<Act>(clDoc, "load act"){

					@Override
					public void onSuccess(Act result) {
						setSelectedAct(result);
					}});
			}
		}, true){

			@Override
			protected void beforeUpdate(List<Act> result) {
				Set<String> classNames = new HashSet<String>();
				for (Act a:result) {
					if (!a.actClass.isSingleton) {
						classNames.add(a.actClass.name);
					}
				}
				int sindex = cmbFilter.getSelectedIndex();
				String selected = sindex > 0 ? cmbFilter.getItemText(sindex) : null;
				sindex = -1;
				cmbFilter.clear();
				cmbFilter.addItem("<Alle anzeigen>");
				for (String s:classNames) {
					if (s.equals(selected)) {
						sindex = cmbFilter.getItemCount();
					}
					cmbFilter.addItem(s);
				}
				
				if (sindex > 0) {
					cmbFilter.setSelectedIndex(sindex);
				}
			}

			@Override
			public boolean addRow(FlexTable table, int row, final Act act) {
				if (act.actClass.isSingleton) {
					addMasterDataTab(act);
				} else {
					int sIndex = cmbFilter.getSelectedIndex();
					String filter = sIndex > 0 ? cmbFilter.getItemText(sIndex) : null;
					
					if (filter == null || filter.equals(act.actClass.name)) {
						int column = 0;
						String imgSource = ActClass.EXTERNAL_DOC.name.equals(act.actClass.name) ? 
								"icons/16/Adobe-PDF-Document-icon.png" : "icons/16/Document-icon.png";
						table.setWidget(row, column++, new Image(imgSource));
						String sDate = act.date != null ? DateTimeFormat.getFormat("dd.MM.yyyy").format(act.date) : "--.--.----";
						table.setWidget(row, column++, new Label(sDate));
						table.setWidget(row, column++, new HTML(act.summary));
						HTML user = new HTML("<i>" + act.modifiedBy.userName + "</i>");
						user.setTitle(act.createdBy.userName);
						table.setWidget(row, column++, user);
					}					
				}
				return !act.actClass.isSingleton;
			}

			};

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
				AddAct.addAct(clDoc, e, historyPanel.getList(), new OnOkHandler<Act>() {

					@Override
					public void onOk(Act act) {
						act.setParticipant(e, Participation.PROTAGONIST, new Date(), null);
						act.setParticipant(e, Participation.ADMINISTRATOR, new Date(), null);
						act.setParticipant(clDoc.getSession().getUser().organisation, Participation.ORGANISATION, new Date(), null);
						
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
		
		cmbFilter.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				historyPanel.refresh();
			}
		});
		historyPanel.addWidget(cmbFilter);
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
	
	private ActRenderer getActRenderer(TabLayoutPanel tab, ActClass actClass) {
		int i = 0;
		ActRenderer ar = null;
		while (i < tab.getWidgetCount()) {
			Widget w = tab.getWidget(i);
			if (w instanceof ActRenderer) {
				Act act = ((ActRenderer)w).getAct();
				if (act.actClass.name.equals(actClass.name)) {
					ar = (ActRenderer)w;
				}
			}
			i++;
		}
		return ar;
	}
	
	protected void addMasterDataTab(Act act) {
		ActRenderer ar = getActRenderer(tab, act.actClass);
		if (ar == null) {
			SRV.actService.findById(clDoc.getSession(), act.id, new DefaultCallback<Act>(clDoc, "reload master data act") {

				@Override
				public void onSuccess(final Act reloaded) {
					SRV.configurationService.getLayoutDefinition(clDoc.getSession(), reloaded.actClass.name, LayoutDefinition.FORM_LAYOUT, new DefaultCallback<LayoutDefinition>(clDoc, "load layout definition") {

						private ActRenderer ar;

						@Override
						public void onSuccess(LayoutDefinition result) {
							ar = new ActRenderer(clDoc,
									new OnOkHandler<Act>() {

										@Override
										public void onOk(Act result) {
											if (result == null) {
												tab.remove(ar);
											}
										}
									}, null);
							ar.setAct(result, reloaded);
							
							tab.add(ar, reloaded.actClass.name);
						}
					});
				}
			});
		} else {
		ar.resetAct(act);
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
			LayoutDefinition ld = layouts.get(act.actClass.name);
			if (ld == null) {
				SRV.configurationService.getLayoutDefinition(clDoc.getSession(), act.actClass.name, LayoutDefinition.FORM_LAYOUT, 
						new DefaultCallback<LayoutDefinition>(clDoc, "getLayoutDef") {

							@Override
							public void onSuccess(LayoutDefinition ld) {
								layouts.put(act.actClass.name, ld);
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

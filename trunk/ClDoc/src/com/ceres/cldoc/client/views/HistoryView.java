package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.INamedValueAccessor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class HistoryView extends DockLayoutPanel {

	// private VerticalPanel historyList = new VerticalPanel();
	private CellList<GenericItem> historyList;
	private GenericItemRenderer viewer;

	private HumanBeing humanBeing;


	
	
	
	public HistoryView(final HumanBeing model) {
		super(Unit.EM);
		this.humanBeing = model;

		historyList = new CellList<GenericItem>(new GenericItemCellRenderer(),
				new ProvidesKey<GenericItem>() {

					@Override
					public Object getKey(GenericItem item) {
						return item.getId();
					}
				});

		SelectionModel<GenericItem> selectionModel = new SingleSelectionModel<GenericItem>() {

			@Override
			public void setSelected(GenericItem valueBag, boolean selected) {
				if (selected && getSelectedObject() != valueBag) {
					if (viewer.setValueBag(valueBag)) {
						super.setSelected(valueBag, selected);
					}
				}
			}
		};
		historyList.setSelectionModel(selectionModel);
		historyList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		// historyList.addCellPreviewHandler(new
		// CellPreviewEvent.Handler<ValueBag> (){
		//
		// @Override
		// public void onCellPreview(CellPreviewEvent<ValueBag> event) {
		// ValueBag vb = event.getValue();
		// if (vb != null) {
		// viewer.setValueBag(vb);
		// }
		// }
		// });


		DockLayoutPanel historyPanel = new DockLayoutPanel(Unit.PX);

		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setStylePrimaryName("buttonsPanel");
		titlePanel.setWidth("100%");
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		HorizontalPanel histButtons = new HorizontalPanel();
		
		Image pbUpload = createButton("upload file", "icons/32/Button-Upload-icon.png");
		Image pbAdd = createButton("add item", "icons/32/File-New-icon.png");
		
		pbUpload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				UploadDialog.uploadFile(model, new OnOkHandler<Void>() {
					
					@Override
					public void onOk(Void result) {
						refresh(null);
					}
				});
			}
		});
		
	
		histButtons.add(pbUpload);
		histButtons.add(pbAdd);
		
		titlePanel.add(histButtons);
		
		ScrollPanel sp = new ScrollPanel(historyList);
		historyPanel.addNorth(titlePanel, 32);
		historyPanel.add(sp);
		historyPanel.addStyleName("searchResults");

		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.addWest(historyPanel, 250);

		viewer = new GenericItemRenderer(new OnOkHandler<GenericItem>() {
			
			@Override
			public void onOk(GenericItem result) {
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
				AddValueBag.addValueBag(humanBeing, new OnOkHandler<GenericItem>() {

					@Override
					public void onOk(GenericItem item) {
						item.addParticipant(model);

						SRV.valueBagService.save(item,
								new DefaultCallback<GenericItem>() {

									@Override
									public void onSuccess(GenericItem item) {
										refresh(item);
									}

								});

					}
				});
			}
		});
		refresh(null);
	}

	private Image createButton(String label, String source) {
		Image img = new Image(source);
		img.setTitle(label);
		return img;
//		return new Button(label + "<img src=\"" + source + "\"/>");
	}

	private void refresh(final GenericItem item) {
		SRV.valueBagService.findByEntity(humanBeing,
				new DefaultCallback<List<GenericItem>>() {

					@Override
					public void onSuccess(List<GenericItem> result) {
						refresh(result, item);
					}
				});
	}

	private void refresh(List<GenericItem> result, GenericItem item) {
		GenericItem selected = ((SingleSelectionModel<GenericItem>)historyList.getSelectionModel()).getSelectedObject();
		historyList.setRowCount(result.size());
		historyList.setRowData(result);
		
		if (item != null) {
			historyList.getSelectionModel().setSelected(item, true);
		} else if (selected != null) {
			historyList.getSelectionModel().setSelected(selected, true);
		}
	}
}

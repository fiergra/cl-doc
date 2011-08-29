package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class HistoryView extends DockLayoutPanel {

	// private VerticalPanel historyList = new VerticalPanel();
	private CellList<ValueBag> historyList;
	private ValueBagEditor viewer;

	private HumanBeing humanBeing;

	public HistoryView(final HumanBeing model) {
		super(Unit.EM);
		this.humanBeing = model;

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Button addNew = new Button("ValueBag");
		hp.setSpacing(8);
		hp.add(new Label("add"));
		hp.add(addNew);

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
		hp.add(form);
		
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				refresh();
			}
		});
		
		hp.add(new Button("Submit", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SRV.configurationService.getUploadUrl(new DefaultCallback<String>() {

					@Override
					public void onSuccess(String result) {
						fileName.setText(fup.getFilename());
						form.setAction(result);
						form.submit();
					}
				});
				System.out.println("do submit..." + fup.getFilename());
			}
		}));


		addNorth(hp, 2.5);

		historyList = new CellList<ValueBag>(new ValueBagCell(),
				new ProvidesKey<ValueBag>() {

					@Override
					public Object getKey(ValueBag item) {
						return item.getId();
					}
				});

		SelectionModel<ValueBag> selectionModel = new SingleSelectionModel<ValueBag>() {

			@Override
			public void setSelected(ValueBag valueBag, boolean selected) {
				if (selected && viewer.setValueBag(valueBag)) {
					super.setSelected(valueBag, selected);
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

		// historyList.setSpacing(3);
		ScrollPanel sp = new ScrollPanel(historyList);
		sp.addStyleName("searchResults");

		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.addWest(sp, 250);

		viewer = new ValueBagEditor(new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		});
		viewer.addStyleName("searchResults");
		splitPanel.add(viewer);

		add(splitPanel);

		addNew.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ValueBag vb = new ValueBag("dummy");

				vb.set("testItBaby!", "asdf");
				vb.addParticipant(model);

				SRV.valueBagService.save(vb, new DefaultCallback<ValueBag>() {

					@Override
					public void onSuccess(ValueBag result) {
						refresh();
					}

				});
			}
		});
		refresh();
	}

	private void refresh() {
		SRV.valueBagService.findByEntity(humanBeing,
				new DefaultCallback<List<ValueBag>>() {

					@Override
					public void onSuccess(List<ValueBag> result) {
						refresh(result);
					}
				});
	}

	private void refresh(List<ValueBag> result) {
		historyList.setRowCount(result.size());
		historyList.setRowData(result);

		// historyList.clear();
		//
		// for (final ValueBag valueBag : result) {
		// historyList.add(new ValueBagSummaryRenderer(valueBag, new
		// ClickHandler() {
		//
		// @Override
		// public void onClick(ClickEvent event) {
		// viewer.setValueBag(valueBag);
		// }
		// }));
		// }
	}
}

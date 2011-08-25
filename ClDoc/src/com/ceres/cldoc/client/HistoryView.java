package com.ceres.cldoc.client;

import java.util.Collection;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HistoryView extends VerticalPanel {

	private ListBox historyList;
	private HumanBeing humanBeing;
	
	public HistoryView(final HumanBeing model) {
		this.humanBeing = model;
		Button addNew = new Button("+");
		add(addNew);
		historyList = new ListBox();
		add(historyList);
		addNew.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ValueBag vb = new ValueBag();
				
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
		SRV.valueBagService.findByEntity(humanBeing, new DefaultCallback<Collection<ValueBag>>() {

			@Override
			public void onSuccess(Collection<ValueBag> result) {
				refresh(result);
			}
		});
	}

	
	private void refresh(Collection<ValueBag> result) {
		historyList.clear();
		
		for (ValueBag valueBag : result) {
			historyList.addItem(valueBag.toString());
		}
	}
}

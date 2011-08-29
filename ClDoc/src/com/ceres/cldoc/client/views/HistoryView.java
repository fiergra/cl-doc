package com.ceres.cldoc.client.views;

import java.util.Collection;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HistoryView extends DockLayoutPanel {

	private VerticalPanel historyList = new VerticalPanel();
	private HumanBeing humanBeing;
	
	public HistoryView(final HumanBeing model) {
		super(Unit.EM);
		this.humanBeing = model;
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Button addNew = new Button("ValueBag");
		hp.setSpacing(3);
		hp.add(new Label("add"));
		hp.add(addNew);
		
		addNorth(hp, 2.5);

		historyList.setSpacing(3);
		ScrollPanel sp = new ScrollPanel(historyList);
		sp.addStyleName("searchResults");

		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
		splitPanel.addWest(sp, 250);
		
		LayoutPanel viewer = new LayoutPanel();
		viewer.addStyleName("searchResults");
		splitPanel.add(viewer);
		
		add(splitPanel);

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
			historyList.add(new ValueBagSummaryRenderer(valueBag));
		}
	}
}

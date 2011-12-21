package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PersonSearchList extends DockLayoutPanel {
	private ClDoc clDoc;

	public PersonSearchList(ClDoc clDoc, final OnClick<HumanBeing> onClickNew, final OnClick<HumanBeing> onClickEdit, final OnClick<HumanBeing> onClickOpen) {
		super(Unit.EM);
		this.clDoc = clDoc;
		setup(onClickNew, onClickEdit, onClickOpen);
	}

	final TextBox searchBox = new TextBox();
	final VerticalPanel verticalList = new VerticalPanel();
	
	private void setup(final OnClick<HumanBeing> onClickNew, final OnClick<HumanBeing> onClickEdit, final OnClick<HumanBeing> onClickOpen) {
		HorizontalPanel hp = new HorizontalPanel();
		final Timer timer = new Timer() {

			@Override
			public void run() {
				doSearch(onClickEdit, onClickOpen);
			}
		};

		searchBox.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				timer.cancel();
				timer.schedule(250);
			}
		});

		hp.setSpacing(2);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.add(new Label("search"));
		searchBox.setWidth("50em");
		hp.add(searchBox);
	
		if (onClickNew != null) {
			Button pbNew = new Button("new...");
			hp.add(pbNew);
			pbNew.setStylePrimaryName("button");
			pbNew.addStyleName("gray");
			pbNew.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					onClickNew.onClick(new HumanBeing());
				}
			});
		}
		
		addNorth(hp, 3);
		verticalList.setWidth("97%");
		verticalList.setSpacing(2);
		ScrollPanel sp = new ScrollPanel(verticalList);
		sp.addStyleName("searchResults");
		add(sp);
	}

	private String lastSearch = "";

	protected void doSearch(final OnClick<HumanBeing> onClickEdit, final OnClick<HumanBeing> onClickOpen) {
		String search = searchBox.getText();
		
		if (!lastSearch.equals(search)) {
			clDoc.status("searching...");
			lastSearch = search;
			searchBox.setEnabled(false);
			SRV.humanBeingService.search(search, new DefaultCallback<List<HumanBeing>>() {

				@Override
				public void onSuccess(List<HumanBeing> result) {
					clDoc.clearStatus();
					verticalList.clear();
					for (final HumanBeing p : result) {
						PersonRenderer pr = new PersonRenderer(p, onClickEdit, onClickOpen);
						pr.setWidth("100%");
						verticalList.add(pr);
					}
					searchBox.setEnabled(true);
				}
			});
		}
	}
	
}

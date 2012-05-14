package com.ceres.cldoc.client.controls;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.model.Act;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ClickableTable<T> extends DockLayoutPanel {

	private final ClDoc clDoc;
	private final FlexTable table;
	private List<T> list;
	private HorizontalPanel buttonsPanel;
	private ListRetrievalService<T> listRetrieval;
	
	public ClickableTable(ClDoc clDoc) {
		super(Unit.PX);
		
		this.clDoc = clDoc;
		table = new FlexTable();
		ScrollPanel sp = new ScrollPanel(table);
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setWidth("100%");
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		titlePanel.setStylePrimaryName("buttonsPanel");
		
		buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(5);
		titlePanel.add(buttonsPanel);
		addNorth(titlePanel, 38);
		sp.addStyleName("resultTable");
		add(sp);

	}

	public ClickableTable(final ClDoc clDoc, 
			final ListRetrievalService<T> listRetrieval, 
			final OnClick<T> onClick, boolean showRefresh) {
		this(clDoc);
		if (showRefresh) {
			Image pbWindowRefresh = addButton("refresh", "icons/32/Window-Refresh-icon.png");
			pbWindowRefresh.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					refresh();
				}
			});
		}

		setOnClick(onClick);
		setListRetrieval(listRetrieval);
		
	}

	public void setListRetrieval(ListRetrievalService<T> listRetrieval) {
		this.listRetrieval = listRetrieval;
	}

	public void setOnClick(final OnClick<T> onClick) {
		table.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Cell cell = table.getCellForEvent(event);
				T entry = list.get(cell.getRowIndex());

				onClick.onClick(entry);
			}
		});

		
	}
	
	public void refresh() {
		listRetrieval.retrieve(null, new DefaultCallback<List<T>>(clDoc, this.getTitle()) {

			@Override
			public void onSuccess(List<T> result) {
				list = result;
				update();
			}
		});
	}

	protected void update() {
		int row = 0;
		table.removeAllRows();
		table.clear();
		RowFormatter rf = table.getRowFormatter();
		rf.addStyleName(row, "resultTable");
		for (T entry:list) {
			addRow(table, row, entry);
			rf.addStyleName(row, "resultRow");
			if (row % 2 == 0) {
				rf.addStyleName(row, "evenRow");
			}
			row++;
		}		
	}

	public abstract void addRow(FlexTable table, int row, T entry);

	public Image addButton(String label, String source) {
		Image img = new Image(source);
		img.setTitle(label);
		buttonsPanel.add(img);
		img.addStyleName("linkButton");
		return img;
	}

	public void addWidget(Widget widget) {
		buttonsPanel.add(widget);
	}

	
	
	public void setSelected(Act result) {
	}
}
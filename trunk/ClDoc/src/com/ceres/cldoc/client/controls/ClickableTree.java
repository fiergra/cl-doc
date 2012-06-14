package com.ceres.cldoc.client.controls;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.views.DefaultCallback;
import com.ceres.cldoc.client.views.OnClick;
import com.ceres.cldoc.model.HasChildren;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public abstract class ClickableTree <T extends HasChildren<T>> extends DockLayoutPanel {

	protected final ClDoc clDoc;
	private final Tree tree;
	private HorizontalPanel buttonsPanel;
	private ListRetrievalService<T> listRetrieval;
	
	public ClickableTree(ClDoc clDoc, boolean showRefresh) {
		super(Unit.PX);
		
		this.clDoc = clDoc;
		tree = new Tree();
		ScrollPanel sp = new ScrollPanel(tree);
		tree.setSize("100%", "100%");
		
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setWidth("100%");
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		titlePanel.setStylePrimaryName("buttonsPanel");
		titlePanel.addStyleName("actTitle");
		buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(5);
		titlePanel.add(buttonsPanel);
		if (showRefresh) {
			Image pbWindowRefresh = addButton("refresh", "icons/32/Window-Refresh-icon.png");
			pbWindowRefresh.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					refresh();
				}
			});
		}
		addNorth(titlePanel, 38);
		add(sp);

	}

	public ClickableTree(final ClDoc clDoc, 
			final ListRetrievalService<T> listRetrieval, 
			final OnClick<T> onClick, boolean showRefresh) {
		this(clDoc, showRefresh);
		setOnClick(onClick);
		setListRetrieval(listRetrieval);
	}

	public void setListRetrieval(ListRetrievalService<T> listRetrieval) {
		this.listRetrieval = listRetrieval;
	}

	public void setOnClick(final OnClick<T> onClick) {
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				onClick.onClick(event.getSelectedItem() != null ? (T)event.getSelectedItem().getUserObject() : null);
			}
		});
	}

	public void setOnDoubleClick(final OnClick<T> onClick) {
	}
	
	public void refresh() {
		tree.clear();
		listRetrieval.retrieve(null, new DefaultCallback<List<T>>(clDoc, this.getTitle()) {

			@Override
			public void onSuccess(List<T> result) {
				TreeItem root = getRoot();
				addTreeItems(root, result);
				if (root != null) {
					tree.addItem(root);
					root.setState(true);
					tree.setSelectedItem(root);
				}
			}
			
			private void addTreeItems(TreeItem parent, List<T> relations) {
				for (T er : relations) {
					TreeItem ti = object2TreeItem(er);
					ti.setUserObject(er);
					if (parent != null) {
						parent.addItem(ti);
					} else {
						tree.addItem(ti);
					}
					if (er.hasChildren()) {
						addTreeItems(ti, er.getChildren());
					}
				}
			}
		
		});
	}

	protected TreeItem getRoot() {
		return null;
	}

	private TreeItem object2TreeItem(T er) {
		Widget renderer = itemRenderer(er);
		return new TreeItem(renderer);
	}

	protected Widget itemRenderer(T er) {
		return new Label(er.toString());
	}

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
}

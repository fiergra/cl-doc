package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class CatalogConfigurator extends DockLayoutPanel {

	private Catalog catalog = new Catalog();
	private HashSet<Catalog> changedObjects = new HashSet<Catalog>();

	private final Image pbSave = new Image("icons/32/Save-icon.png");
	private final Image pbNew = new Image("icons/32/File-New-icon.png");
	private final Image pbDelete = new Image("icons/32/File-Delete-icon.png");
	private Collection<Catalog> catalogs;
	private ClDoc clDoc;
	
	
	public CatalogConfigurator(ClDoc clDoc) {
		super(Unit.PX);
		this.clDoc = clDoc;
		setup();
	}

	private void setup() {
		HorizontalPanel title = new HorizontalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		final Tree tree = new Tree();

		title.setStylePrimaryName("buttonsPanel");
		title.setWidth("100%");
		title.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		title.add(buttons);
		buttons.setSpacing(5);
		pbSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SRV.configurationService.saveAll(clDoc.getSession(), changedObjects,
						new DefaultCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								refreshTree(tree);
							}

						});
				changedObjects.clear();
			}
		});

		final CellTable<Catalog> childGrid = new CellTable<Catalog>();
		setupGrid(childGrid);

		pbNew.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TreeItem selected = tree.getSelectedItem();
				if (selected != null) {
					addNewCatalog(selected, childGrid);
				}
			}
		});
		
		pbDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TreeItem selected = tree.getSelectedItem();
				if (selected != null) {
					SRV.configurationService.delete(clDoc.getSession(), (Catalog)selected.getUserObject(), 						new DefaultCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							refreshTree(tree);
						}

					});

				}
			}
		});
		
		buttons.add(pbNew);
		buttons.add(pbSave);
		buttons.add(pbDelete);

		AbstractCell<Catalog> cell = new AbstractCell<Catalog>() {

			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Catalog value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<tr><td>" + value.code + "</td><td>"
						+ value.shortText + "</td><td>" + value.text
						+ "</td></tr>");

			}
		};

		SplitLayoutPanel mainSplit = new SplitLayoutPanel();
		mainSplit.addWest(tree, 300);
		mainSplit.add(childGrid);
		refreshTree(tree);
		
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				populateChildGrid(childGrid, event.getSelectedItem());
			}
		});

		addNorth(title, 36);
		add(mainSplit);

	}
	
	protected void addNewCatalog(TreeItem selectedItem, final CellTable<Catalog> childGrid) {
		Catalog parent = (Catalog) selectedItem.getUserObject();
		Catalog child = new Catalog();
		if (parent != null) {
			child.parent = parent;
			child.code = parent.code + "<change me>";
		} else {
			child.parent = null;
			child.code = "<change me>";
		}
		child.shortText = "";
		child.text = "";
		addToChangedObjects(child);
		
		selectedItem.addItem(catalog2TreeItem(child));
		catalogs.add(child);
		updateRowData(childGrid);
	}


	protected void populateChildGrid(final CellTable<Catalog> childGrid,
			TreeItem selectedItem) {
		Catalog c = (Catalog) (selectedItem != null ? selectedItem
				.getUserObject() : null);

		SRV.configurationService.listCatalogs(clDoc.getSession(), c,
				new DefaultCallback<Collection<Catalog>>() {

					@Override
					public void onSuccess(Collection<Catalog> result) {
						catalogs = result;
						updateRowData(childGrid);
					}
				});
	}

	private void updateRowData(final CellTable<Catalog> childGrid) {
		childGrid.setRowCount(catalogs.size());
		childGrid.setRowData(new ArrayList<Catalog>(catalogs));
		
	}
	private void setupGrid(CellTable<Catalog> childGrid) {
		childGrid.setWidth("100%");
		Column<Catalog, String> colCode = new Column<Catalog, String>(
				new EditTextCell()) {
			@Override
			public String getValue(Catalog object) {
				return object.code;
			}
		};

		colCode.setFieldUpdater(new FieldUpdater<Catalog, String>() {
		      public void update(int index, Catalog object, String value) {
		        object.code = value;
		        addToChangedObjects(object);
		      }
		    });

		
		childGrid.addColumn(colCode, "Code");
		childGrid.setColumnWidth(colCode, 10, Unit.PCT);

		Column<Catalog, String> colShort = new Column<Catalog, String>(
				new EditTextCell()) {
			@Override
			public String getValue(Catalog object) {
				return object.shortText;
			}
		};
		colShort.setFieldUpdater(new FieldUpdater<Catalog, String>() {
		      public void update(int index, Catalog object, String value) {
		        object.shortText = value;
		        addToChangedObjects(object);
		      }
		    });

		childGrid.addColumn(colShort, "Short");
		childGrid.setColumnWidth(colShort, 30, Unit.PCT);

		Column<Catalog, String> colText = new Column<Catalog, String>(
				new EditTextCell()) {
			@Override
			public String getValue(Catalog object) {
				return object.text;
			}
		};

		colText.setFieldUpdater(new FieldUpdater<Catalog, String>() {
		      public void update(int index, Catalog object, String value) {
		        object.text = value;
		        addToChangedObjects(object);
		      }
		    });
		
		childGrid.addColumn(colText, "Text");
		childGrid.setColumnWidth(colText, 60, Unit.PCT);
		
	}

	protected void addToChangedObjects(Catalog object) {
		changedObjects.add(object);
	}

	protected void fromDialog(Grid form) {
		int row = 0;
		catalog.code = ((TextBoxBase) form.getWidget(row++, 1)).getText();
		catalog.shortText = ((TextBoxBase) form.getWidget(row++, 1)).getText();
		catalog.text = ((TextBoxBase) form.getWidget(row++, 1)).getText();
	}

	protected void toDialog(Grid form) {
		int row = 0;
		((TextBoxBase) form.getWidget(row++, 1)).setText(catalog.code);
		((TextBoxBase) form.getWidget(row++, 1)).setText(catalog.shortText);
		((TextBoxBase) form.getWidget(row++, 1)).setText(catalog.text);
	}

	void setupForm(Grid grid) {
		int row = 0;
		grid.setWidget(row, 0, new Label("code"));
		grid.setWidget(row, 1, new TextBox());
		row++;
		grid.setWidget(row, 0, new Label("sort text"));
		grid.setWidget(row, 1, new TextBox());
		row++;
		grid.setWidget(row, 0, new Label("text"));
		grid.setWidget(row, 1, new TextBox());
		row++;
	}

	private void refreshTree(final Tree tree) {
		tree.clear();

		SRV.configurationService.listCatalogs(clDoc.getSession(), (Catalog)null,
				new DefaultCallback<Collection<Catalog>>() {

					@Override
					public void onSuccess(Collection<Catalog> result) {
						TreeItem root = new TreeItem("root");
						addTreeItems(root, result);
						tree.addItem(root);
						tree.setSelectedItem(root);
					}
					
					private void addTreeItems(TreeItem parent, Collection<Catalog> children) {
						for (Catalog c : children) {
							TreeItem ti = catalog2TreeItem(c);
							parent.addItem(ti);
							if (c.hasChildren()) {
								addTreeItems(ti, c.children);
							}
						}
					}
					
					
				});
	}

	private TreeItem catalog2TreeItem(Catalog c) {
		TreeItem ti = new TreeItem("<b>" + c.code + "</b><i>" + c.shortText + "</i>");
		ti.setUserObject(c);
		return ti;
	}

}

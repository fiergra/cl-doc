package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.LongLink;
import com.ceres.dynamicforms.client.TextLink;
import com.ceres.dynamicforms.client.components.LongTextBox;
import com.ceres.dynamicforms.client.components.MapListRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class CatalogConfigurator2 extends DockLayoutPanel {

	private final Image pbSave = new Image("icons/32/Save-icon.png");
	private final Image pbNew = new Image("icons/32/File-New-icon.png");
	private final Image pbDelete = new Image("icons/32/File-Delete-icon.png");
	private final Image pbUpload = new Image("icons/32/Button-Upload-icon.png");
	private final Image pbDownload = new Image("icons/32/Button-Download-icon.png");

	private final ClDoc clDoc;
	
	
	public CatalogConfigurator2(ClDoc clDoc) {
		super(Unit.EM);
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
		final Label selectionLabel = new Label("-");
		selectionLabel.setWidth("200px");
		title.add(selectionLabel);
		title.add(buttons);
		buttons.setSpacing(5);

		final String[] fieldNames = new String[]{"code", "shortText", "text", "logicalOrder"};
		final MapListRenderer nvl = new MapListRenderer(new String[]{"Code", "Kurztext", "Text"}, null) {
			
			@Override
			protected
			Map<String,Serializable> newAct() {
				Catalog parent = (Catalog) tree.getSelectedItem().getUserObject();
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
//				addToChangedObjects(child);
				
//				catalogs.add(child);

				return new CatalogWrapper(child);
			}
			
			@Override
			protected boolean isValid(Interactor interactor) {
				return true;
			}

			@Override
			protected void createNewRow(int row, Interactor interactor) {
				int col = 0;
				TextBox textBox = new TextBox();
				setWidget(row, col, textBox);
				interactor.addLink(new TextLink(interactor, fieldNames[col], textBox, null));
				col++;
				textBox = new TextBox();
				setWidget(row, col, textBox);
				interactor.addLink(new TextLink(interactor, fieldNames[col], textBox, null));
				col++;
				textBox = new TextBox();
//				textBox.setWidth("100%");
				setWidget(row, col, textBox);
				interactor.addLink(new TextLink(interactor, fieldNames[col], textBox, null));
				col++;
				
				LongTextBox ltb = new LongTextBox();
				setWidget(row, col, ltb);
				interactor.addLink(new LongLink(interactor, fieldNames[col], ltb, null));
				col++;
			}

			@Override
			protected boolean canRemove(final Map<String,Serializable> act) {
				Catalog catalog = ((CatalogWrapper)act).unwrap();
				
				if (catalog.id != null) {
					deleteCatalog(catalog, new Runnable() {
						
						@Override
						public void run() {
							removeAct(act);
						}
					});
					return false;
				} else {
					return true;
				}
			}
		};
		
		pbDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final TreeItem selected = tree.getSelectedItem();
				if (selected != null) {
					deleteCatalog((Catalog)selected.getUserObject(), new Runnable() {
						@Override
						public void run() {
							refreshTree(tree);
						}
					});
				}
			}
		});

		pbSave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<Catalog> catalogs = unwrap(nvl.getChangedObjects());
				SRV.configurationService.saveAll(clDoc.getSession(), catalogs,
						new DefaultCallback<Void>(clDoc, "saveAll") {

							@Override
							public void onSuccess(Void result) {
								refreshTree(tree);
							}

						});
//				changedObjects.clear();
			}
		});

		pbUpload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				UploadDialog.uploadCatalogs(clDoc, new OnOkHandler<Void>() {
					
					@Override
					public void onOk(Void result) {
						refreshTree(tree);
					}
				});
			}
		});

		pbDownload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=catalogs", "_blank", "");
			}
		});

		
		buttons.add(pbDownload);
		buttons.add(pbUpload);
		
		buttons.add(pbNew);
		buttons.add(pbSave);
		buttons.add(pbDelete);

		SplitLayoutPanel mainSplit = new SplitLayoutPanel();
		mainSplit.addWest(new ScrollPanel(tree), 300);
		mainSplit.add(new ScrollPanel(nvl));
		refreshTree(tree);
		
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem selected = event.getSelectedItem();
				if (selected != null && selected.getUserObject() != null) {
					selectionLabel.setText(((Catalog)selected.getUserObject()).toString());
				} else {
					selectionLabel.setText("-");
				}
				populateChildGrid(nvl, selected);
			}
		});

		addNorth(title, 3);
		add(mainSplit);

	}
	
	protected void deleteCatalog(final Catalog catalog, final Runnable afterDelete) {
		new MessageBox("Loeschen", "Wollen Sie den ausgewaehlten Katalog loeschen?", MessageBox.MB_YES | MessageBox.MB_NO, MessageBox.MESSAGE_ICONS.MB_ICON_QUESTION){

			@Override
			protected void onClick(int result) {
				if (result == MessageBox.MB_YES) {
					SRV.configurationService.delete(clDoc.getSession(), catalog, 						
							new DefaultCallback<Void>(clDoc, "delete") {

						@Override
						public void onSuccess(Void result) {
							afterDelete.run();
						}

					});
				}
			}};
	}

	protected List<Catalog> unwrap(Collection<Map<String,Serializable>> changedObjects) {
		List<Catalog> result = new ArrayList<Catalog>(changedObjects.size());
		for (Map<String,Serializable> inv:changedObjects) {
			result.add(((CatalogWrapper)inv).unwrap());
		}
		return result;
	}

	//	protected void addNewCatalog(TreeItem selectedItem, final CellTable<Catalog> childGrid) {
//		Catalog parent = (Catalog) selectedItem.getUserObject();
//		Catalog child = new Catalog();
//		if (parent != null) {
//			child.parent = parent;
//			child.code = parent.code + "<change me>";
//		} else {
//			child.parent = null;
//			child.code = "<change me>";
//		}
//		child.shortText = "";
//		child.text = "";
//		addToChangedObjects(child);
//		
//		selectedItem.addItem(catalog2TreeItem(child));
//		catalogs.add(child);
//	}
//
//
	protected void populateChildGrid(final MapListRenderer nvl,
			TreeItem selectedItem) {
		Catalog c = (Catalog) (selectedItem != null ? selectedItem
				.getUserObject() : null);

		SRV.catalogService.listCatalogs(clDoc.getSession(), c,
				new DefaultCallback<List<Catalog>>(clDoc, "listCatalogs") {

					@Override
					public void onSuccess(List<Catalog> result) {
						nvl.setActs(wrap(result));
					}
				});
	}

//	private void updateRowData(final NamedValuesListRenderer nvl) {
//		nvl.setActs(wrap(catalogs));
//	}
	
	private List<Map<String,Serializable>> wrap(List<Catalog> catalogs) {
		List<Map<String,Serializable>> result = new ArrayList<Map<String,Serializable>>(catalogs.size());
		for (Catalog c:catalogs) {
			result.add(new CatalogWrapper(c));
		}
		return result;
	}

//	protected void addToChangedObjects(Catalog object) {
//		changedObjects.add(object);
//	}
//
//	protected void fromDialog(Grid form) {
//		int row = 0;
//		catalog.code = ((TextBoxBase) form.getWidget(row++, 1)).getText();
//		catalog.shortText = ((TextBoxBase) form.getWidget(row++, 1)).getText();
//		catalog.text = ((TextBoxBase) form.getWidget(row++, 1)).getText();
//	}
//
//	protected void toDialog(Grid form) {
//		int row = 0;
//		((TextBoxBase) form.getWidget(row++, 1)).setText(catalog.code);
//		((TextBoxBase) form.getWidget(row++, 1)).setText(catalog.shortText);
//		((TextBoxBase) form.getWidget(row++, 1)).setText(catalog.text);
//	}
//
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
		SRV.catalogService.listCatalogs(clDoc.getSession(), (Catalog)null,
				new DefaultCallback<List<Catalog>>(clDoc, "listCatalogs") {

					@Override
					public void onSuccess(List<Catalog> result) {
						@SuppressWarnings("deprecation")
						TreeItem root = new TreeItem("root");
						addTreeItems(root, result);
						tree.addItem(root);
						root.setState(true);
						tree.setSelectedItem(root);
					}
					
					private void addTreeItems(TreeItem parent, Collection<Catalog> children) {
						for (Catalog c : children) {
							TreeItem ti = catalog2TreeItem(c);
							parent.addItem(ti);
							ti.setState(true);
							if (c.hasChildren()) {
								addTreeItems(ti, c.children);
							}
						}
					}
					
					
				});
	}

	private TreeItem catalog2TreeItem(Catalog c) {
		@SuppressWarnings("deprecation")
		TreeItem ti = new TreeItem(c.id + "|<b>" + c.code + "</b> - <i>" + c.shortText + "</i>");
		ti.setUserObject(c);
		return ti;
	}

}

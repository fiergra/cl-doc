package com.ceres.cldoc.client.views;


import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandChangeListener;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Styler extends DockLayoutPanel {
	private IForm form;
	private final ClDoc clDoc;
	
	public Styler(ClDoc clDoc) {
		super(Unit.PX);
		this.clDoc = clDoc;
		setup();
	}


	private void setup() {
		final TextArea formLayoutDescTextArea = new TextArea();
		final Grid classDef = new Grid(5, 2);
		classDef.setWidth("100%");
		classDef.addStyleName("docform");

		final DockLayoutPanel formLayoutPanel = new DockLayoutPanel(Unit.EM);
		final TextArea printLayoutDescTextArea = new TextArea();
		final DockLayoutPanel printLayoutPanel = new DockLayoutPanel(Unit.EM);
//		final CheckBox cbMasterData = new CheckBox("Stammdaten");

		formLayoutDescTextArea.addStyleName("sourceCode");
		printLayoutPanel.addStyleName("sourceCode");
		
		final TextBox txtName = new TextBox();
		txtName.setEnabled(false);
		
		final TextArea txtSummary = new TextArea();
		txtSummary.setWidth("100%");
		
		final HTML txtPreview = new HTML();
		txtPreview.setWidth("100%");

		txtSummary.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				txtPreview.setHTML(txtSummary.getText());
			}
		});
		
		final CheckBox cbSingleton = new CheckBox("");
		final CatalogListBox lbEntityTypes = new CatalogListBox(clDoc, "MASTERDATA.EntityTypes");
		
		formLayoutPanel.add(formLayoutDescTextArea);
		printLayoutPanel.add(printLayoutDescTextArea);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setSpacing(5);

//		hp.add(cbMasterData);
		hp.add(new Label("class"));
		
		final DockLayoutPanel formContainer = new DockLayoutPanel(Unit.EM);
		final Runnable updateForm = new Runnable() {
			
			@Override
			public void run() {
				if (form != null) {
					int index = formContainer.getWidgetIndex(form);
					formContainer.remove(form);
				}
				form = ActRenderer.getActRenderer(clDoc, formLayoutDescTextArea.getText(), null, null);
				formContainer.add(form);
			}
		};
		
		final OnDemandComboBox<ActClass> cmbClasses = new OnDemandComboBox<ActClass>(clDoc, 
				new ListRetrievalService<ActClass>() {

			@Override
			public void retrieve(String filter,
					AsyncCallback<List<ActClass>> callback) {
				SRV.configurationService.listClasses(clDoc.getSession(), filter, callback);
			}
		}, new LabelFunction<ActClass>() {

			@Override
			public String getLabel(ActClass actClass) {
				return actClass.name;
			}

			@Override
			public String getValue(ActClass actClass) {
				return String.valueOf(actClass.id);
			}
		}, null);
		cmbClasses.addSelectionChangedHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				ActClass newValue = cmbClasses.getSelected(); 
				if (newValue != null) {
					AsyncCallback<LayoutDefinition> callback = new DefaultCallback<LayoutDefinition>(clDoc, "") {

						@Override
						public void onSuccess(LayoutDefinition result) {
							if (result != null) {
								if (result.type == LayoutDefinition.FORM_LAYOUT/* || result.type == LayoutDefinition.MASTER_DATA_LAYOUT*/) {
									formLayoutDescTextArea.setText(result.xmlLayout);
									txtName.setText(result.actClass.name);
									txtSummary.setText(result.actClass.summaryDef);
									txtPreview.setHTML(result.actClass.summaryDef);
									cbSingleton.setValue(result.actClass.isSingleton);
									lbEntityTypes.setSelected(result.actClass.entityType);
									updateForm.run();
								} else {
									printLayoutDescTextArea.setText(result.xmlLayout);
								}
							}
						}
					};
					formLayoutDescTextArea.setText(null);
					SRV.configurationService.getLayoutDefinition(clDoc.getSession(), newValue.name, 
							/*cbMasterData.getValue() ? LayoutDefinition.MASTER_DATA_LAYOUT : */LayoutDefinition.FORM_LAYOUT, callback );
					printLayoutDescTextArea.setText(null);
					SRV.configurationService.getLayoutDefinition(clDoc.getSession(), newValue.name, LayoutDefinition.PRINT_LAYOUT, callback );
				}
			}
		});
		cmbClasses.setWidth("200px");
		hp.add(cmbClasses);

		int row = 0;
		addLine(classDef, row++, "Name", txtName); 
		addLine(classDef, row++, "Zusammenfassung", txtSummary); 
		addLine(classDef, row++, "Vorschau", txtPreview); 
		addLine(classDef, row++, "Stammdatum", cbSingleton); 
		addLine(classDef, row++, "Entitaet", lbEntityTypes); 
		
//		hp.add(new Label("Summary"));
//		hp.add(txtSummary);
//		hp.add(cbSingleton);
//		hp.add(lbEntityTypes);

		Image pbUpload = new Image("icons/32/Button-Upload-icon.png");
		pbUpload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				UploadDialog.uploadLayouts(clDoc, LayoutDefinition.FORM_LAYOUT, new OnOkHandler<Void>() {
					
					@Override
					public void onOk(Void result) {
						
					}
				});
			}
		});
//		Anchor a = new Anchor("<img src=\"icons/32/Button-Download-icon.png\"/>", true);
//		String baseUrl = GWT.getModuleBaseURL();
//		a.setHref(baseUrl + "download?type=form_layouts");
//		hp.add(a);
		
		Image pbDownload = new Image("icons/32/Button-Download-icon.png");
		pbDownload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=form_layouts", "_blank", "");
			}
		});
		hp.add(pbDownload);
		hp.add(pbUpload);

		Image pbWindowRefresh = new Image("icons/32/Window-Refresh-icon.png");
		pbWindowRefresh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateForm.run();
			}
		});
		hp.add(pbWindowRefresh);
		
		Image pbSave = new Image("icons/32/Save-icon.png");
		pbSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (cmbClasses.getText() != null && cmbClasses.getText().length() > 1) {
					AsyncCallback<LayoutDefinition> callback = new DefaultCallback<LayoutDefinition>(clDoc, "saveLayout") {
	
						@Override
						public void onSuccess(LayoutDefinition result) {
							cmbClasses.refresh();
						}
					};
					ActClass actClass = cmbClasses.getSelected();
					if (actClass == null) {
						actClass = new ActClass();
						actClass.name = cmbClasses.getText();
					}
					actClass.entityType = lbEntityTypes.getSelected() != null ? lbEntityTypes.getSelected().id : null;
					actClass.isSingleton = cbSingleton.getValue();
					actClass.summaryDef = txtSummary.getText();
					
					LayoutDefinition ld = new LayoutDefinition(
							actClass,
							/*cbMasterData.getValue() ? LayoutDefinition.MASTER_DATA_LAYOUT :  */LayoutDefinition.FORM_LAYOUT, 
									formLayoutDescTextArea.getText());
					SRV.configurationService.saveLayoutDefinition(clDoc.getSession(), ld, callback);
					if (printLayoutDescTextArea.getText() != null && printLayoutDescTextArea.getText().length() > 0) {
						SRV.configurationService.saveLayoutDefinition(clDoc.getSession(), 
								new LayoutDefinition(actClass, LayoutDefinition.PRINT_LAYOUT, printLayoutDescTextArea.getText()), callback);
					}
				}
			}
		});
		hp.add(pbSave);
		
		Image pbDelete = new Image("icons/32/File-Delete-icon.png");
		pbDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ActClass actClass = cmbClasses.getSelected();
				if (actClass != null) {
					SRV.configurationService.deleteLayoutDefinition(clDoc.getSession(), actClass.name, new DefaultCallback<Void>(clDoc, "deleteLayout") {
	
						@Override
						public void onSuccess(Void result) {
	//						refreshOracle();
						}
					});
				}
			}
		});
		hp.add(pbDelete);

		
//		refreshOracle();
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName("buttonsPanel");
		buttons.add(hp);
		addNorth(buttons, 38);
		
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
			
		TabLayoutPanel layouts = new TabLayoutPanel(2, Unit.EM);
		layouts.add(classDef, "Classdef");
		layouts.add(formLayoutPanel, "Layout");
		layouts.add(printLayoutPanel, "Printout");
		
		splitPanel.addWest(layouts, 400);
		splitPanel.add(formContainer);
		add(splitPanel);
		
//		formLayoutDescTextArea.addKeyUpHandler(new KeyUpHandler() {
//			
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				updateForm.run();
//			}
//		});
		
		formLayoutDescTextArea.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateForm.run();
			}
		});
	}


	private void addLine(Grid classDef, int row, String label, Widget w) {
		Label l = new Label(label);
		classDef.setWidget(row, 0, l);
		classDef.getCellFormatter().addStyleName(row, 0, "formLabel");	
		classDef.setWidget(row, 1, w);
	}


//	private void refreshOracle() {
//		mwso.clear();
//		SRV.configurationService.listLayoutDefinitions(clDoc.getSession(), null, new DefaultCallback<List<LayoutDefinition>>(clDoc, "listLayout") {
//
//			@Override
//			public void onSuccess(List<LayoutDefinition> result) {
//				for (LayoutDefinition fcd : result) {
//					mwso.add(fcd.name);
//				}
//				
//			}
//		});
//		
//	}
//

}

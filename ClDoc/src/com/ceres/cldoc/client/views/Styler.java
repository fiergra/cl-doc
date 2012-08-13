package com.ceres.cldoc.client.views;


import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandChangeListener;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public class Styler extends DockLayoutPanel {
	private IView<Act> form;
	private final ClDoc clDoc;
	
	public Styler(ClDoc clDoc) {
		super(Unit.PX);
		this.clDoc = clDoc;
		setup();
	}


	private void setup() {
		final TextArea formLayoutDescTextArea = new TextArea();
		final DockLayoutPanel formLayoutPanel = new DockLayoutPanel(Unit.EM);
		final TextArea printLayoutDescTextArea = new TextArea();
		final DockLayoutPanel printLayoutPanel = new DockLayoutPanel(Unit.EM);
		final CheckBox cbMasterData = new CheckBox("Stammdaten");

		formLayoutPanel.add(formLayoutDescTextArea);
		printLayoutPanel.add(printLayoutDescTextArea);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setSpacing(5);

		hp.add(cbMasterData);
		hp.add(new Label("class"));
		
		final DockLayoutPanel formContainer = new DockLayoutPanel(Unit.EM);
		final Runnable updateForm = new Runnable() {
			
			@Override
			public void run() {
				if (form != null) {
					formContainer.remove(form);
				}
				form = ActRenderer.getActRenderer(clDoc, formLayoutDescTextArea.getText(), null, null);
				formContainer.add(form);
			}
		};
		
		final OnDemandComboBox<String> cmbClasses = new OnDemandComboBox<String>(clDoc, new ListRetrievalService<String>() {

			@Override
			public void retrieve(String filter,
					AsyncCallback<List<String>> callback) {
				SRV.configurationService.listClassNames(clDoc.getSession(), filter, callback);
			}
		}, new LabelFunction<String>() {

			@Override
			public String getLabel(String className) {
				return className;
			}

			@Override
			public String getValue(String className) {
				return className;
			}
		}, new OnDemandChangeListener<String>() {

			@Override
			public void onChange(String oldValue, String newValue) {
				String className = newValue;
				
				if (className != null) {
					AsyncCallback<LayoutDefinition> callback = new DefaultCallback<LayoutDefinition>(clDoc, "") {

						@Override
						public void onSuccess(LayoutDefinition result) {
							if (result != null) {
								if (result.type == LayoutDefinition.FORM_LAYOUT || result.type == LayoutDefinition.MASTER_DATA_LAYOUT) {
									formLayoutDescTextArea.setText(result.xmlLayout);
									updateForm.run();
								} else {
									printLayoutDescTextArea.setText(result.xmlLayout);
								}
							}
						}
					};
					formLayoutDescTextArea.setText(null);
					SRV.configurationService.getLayoutDefinition(clDoc.getSession(), className, 
							cbMasterData.getValue() ? LayoutDefinition.MASTER_DATA_LAYOUT : LayoutDefinition.FORM_LAYOUT, callback );
					printLayoutDescTextArea.setText(null);
					SRV.configurationService.getLayoutDefinition(clDoc.getSession(), className, LayoutDefinition.PRINT_LAYOUT, callback );
				}
			}
		});
		cmbClasses.setWidth("200px");
		hp.add(cmbClasses);
//		hp.add(lbClasses);

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
					AsyncCallback<Void> callback = new DefaultCallback<Void>(clDoc, "saveLayout") {
	
						@Override
						public void onSuccess(Void result) {}
					};
					SRV.configurationService.saveLayoutDefinition(clDoc.getSession(), cbMasterData.getValue() ? LayoutDefinition.MASTER_DATA_LAYOUT :  LayoutDefinition.FORM_LAYOUT, cmbClasses.getText(), formLayoutDescTextArea.getText(), callback);
					if (printLayoutDescTextArea.getText() != null && printLayoutDescTextArea.getText().length() > 0) {
						SRV.configurationService.saveLayoutDefinition(clDoc.getSession(), LayoutDefinition.PRINT_LAYOUT, cmbClasses.getText(), printLayoutDescTextArea.getText(), callback);
					}
				}
			}
		});
		hp.add(pbSave);
		
		Image pbDelete = new Image("icons/32/File-Delete-icon.png");
		pbDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String ld = cmbClasses.getSelected();
				if (ld != null) {
					SRV.configurationService.deleteLayoutDefinition(clDoc.getSession(), ld, new DefaultCallback<Void>(clDoc, "deleteLayout") {
	
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

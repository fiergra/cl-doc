package com.ceres.cldoc.client.views;


import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LabelFunction;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.controls.OnDemandComboBox;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

public class Styler extends DockLayoutPanel {
	private MultiWordSuggestOracle mwso;

	final Form <GenericItem> form;

	private ClDoc clDoc;
	
	public Styler(ClDoc clDoc) {
		super(Unit.PX);
		this.clDoc = clDoc;
		form = new Form<GenericItem>(clDoc.getSession(), new GenericItem("dummy"), null){

			@Override
			protected void setup() {
				setWidth("100%");
			}};
		setup();
	}


	private void setup() {
		final TextArea layoutDesc = new TextArea();
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setSpacing(5);
		hp.add(new Label("class"));
		mwso = new MultiWordSuggestOracle(){
			@Override
			public void requestSuggestions(Request request, final Callback callback) {
				
				Callback cb = new Callback() {
					
					@Override
					public void onSuggestionsReady(Request request, Response response) {
						if (!response.hasMoreSuggestions()) {
							layoutDesc.setText("");
						}
						callback.onSuggestionsReady(request, response);
						
					}
				};
				super.requestSuggestions(request, cb );
			}};
		final SuggestBox lbClasses = new SuggestBox(mwso);
		lbClasses.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				String className = event.getSelectedItem().getReplacementString();
				
				if (className != null) {
					AsyncCallback<LayoutDefinition> callback = new DefaultCallback<LayoutDefinition>() {

						@Override
						public void onSuccess(LayoutDefinition result) {
							layoutDesc.setText(result.xmlLayout);
							form.parseAndCreate(result.xmlLayout);
						}
					};
					SRV.configurationService.getLayoutDefinition(clDoc.getSession(), className, callback );
				}
			}
		});
		
		OnDemandComboBox<LayoutDefinition> cmbClasses = new OnDemandComboBox<LayoutDefinition>(new ListRetrievalService<LayoutDefinition>() {

			@Override
			public void retrieve(String filter,
					AsyncCallback<List<LayoutDefinition>> callback) {
				SRV.configurationService.listLayoutDefinitions(clDoc.getSession(), filter, callback);
			}
		}, new LabelFunction<LayoutDefinition>() {

			@Override
			public String getLabel(LayoutDefinition item) {
				return item.name;
			}

			@Override
			public String getValue(LayoutDefinition item) {
				return item.name;
			}
		});
		cmbClasses.setWidth("150px");
		hp.add(cmbClasses);
		hp.add(lbClasses);

		Image pbWindowRefreh = new Image("icons/32/Window-Refresh-icon.png");
		pbWindowRefreh.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				form.parseAndCreate(layoutDesc.getText());
			}
		});
		hp.add(pbWindowRefreh);
		
		Image pbSave = new Image("icons/32/Save-icon.png");
		pbSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SRV.configurationService.saveLayoutDefinition(clDoc.getSession(), lbClasses.getText(), layoutDesc.getText(), new DefaultCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						refreshOracle();
					}
				});
			}
		});
		hp.add(pbSave);
		
		Image pbDelete = new Image("icons/32/File-Delete-icon.png");
		pbDelete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SRV.configurationService.deleteLayoutDefinition(clDoc.getSession(), lbClasses.getText(), new DefaultCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						refreshOracle();
					}
				});
			}
		});
		hp.add(pbDelete);

		
		refreshOracle();
		addNorth(hp, 38);
		
		SplitLayoutPanel splitPanel = new SplitLayoutPanel();
			
		layoutDesc.setText("<form><line label=\"label\" type=\"String\"/></form>");
		
		TabLayoutPanel layouts = new TabLayoutPanel(2, Unit.EM);
		layouts.add(layoutDesc, "Layout");
		layouts.add(new Label("follow"), "Printout");
		
		splitPanel.addWest(layouts, 400);
		HorizontalPanel formContainer = new HorizontalPanel();
		formContainer.add(form);
		splitPanel.add(formContainer);
		add(splitPanel);
		
		layoutDesc.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				form.parseAndCreate(layoutDesc.getText());
			}
		});
	}


	private void refreshOracle() {
		mwso.clear();
		SRV.configurationService.listLayoutDefinitions(clDoc.getSession(), null, new DefaultCallback<List<LayoutDefinition>>() {

			@Override
			public void onSuccess(List<LayoutDefinition> result) {
				for (LayoutDefinition fcd : result) {
					mwso.add(fcd.name);
				}
				
			}
		});
		
	}


}

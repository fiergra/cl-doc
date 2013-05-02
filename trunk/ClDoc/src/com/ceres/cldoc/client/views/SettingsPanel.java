package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class SettingsPanel extends DockLayoutPanel {

	public SettingsPanel(ClDoc clDoc) {
		super(Unit.EM);
		setup(clDoc);
	}

	private void setup(final ClDoc clDoc) {
		addStyleName("docform");
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setStyleName("buttonsPanel");
		HorizontalPanel buttons = new HorizontalPanel();
		buttonsPanel.add(buttons);
		buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonsPanel.setWidth("100%");

		final TextBox lucenePathBox = new TextBox(); 
		final TextBox docArchivePathBox = new TextBox(); 
		lucenePathBox.setWidth("90%");
		docArchivePathBox.setWidth("90%");

		
		final LinkButton pbSave = new LinkButton("Speichern", "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AsyncCallback<Void> NOP = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
					}
				};
				
				String pathName = docArchivePathBox.getText();
				if (pathName.length() > 0) {
					SRV.configurationService.setDocArchivePath(clDoc.getSession(), pathName, NOP);
				}

				pathName = lucenePathBox.getText();
				if (pathName.length() > 0) {
					SRV.configurationService.setLuceneIndexPath(clDoc.getSession(), pathName, NOP);
				}
			}});

		pbSave.enable(true);
		buttons.add(pbSave);
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Form form = new Form(clDoc, null, new Runnable() {
			
			@Override
			public void run() {
				pbSave.enable(true);
			}}, null) {

			@Override
			protected void setup() {
				addLabeledWidget("Lucene index path", true, lucenePathBox);
				addLabeledWidget("DocArchive path", true, docArchivePathBox);
			}

			@Override
			public void toDialog() {
				SRV.configurationService.getLuceneIndexPath(new DefaultCallback<String>(clDoc, "getLucenePath") {

					@Override
					public void onSuccess(String text) {
						lucenePathBox.setText(text);
					}
				});
				SRV.configurationService.getDocArchivePath(new DefaultCallback<String>(clDoc, "getDocArchivePath") {

					@Override
					public void onSuccess(String text) {
						docArchivePathBox.setText(text);
					}
				});
				super.toDialog();
			}
			
		};
		
		addNorth(buttonsPanel, 3);
		form.setWidth("100%");
		add(form);
	}

}

package com.ceres.cldoc.client.views;

import com.ceres.cldoc.IDocArchive;
import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.FileSystemNode;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
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
		final HorizontalPanel hpLucene = new HorizontalPanel();
		final PushButton pbLucene = new PushButton("...");
		pbLucene.setPixelSize(24, 24);
		hpLucene.setWidth("100%");
		hpLucene.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpLucene.add(lucenePathBox);
		hpLucene.add(pbLucene);
		pbLucene.addClickHandler(new ClickHandler() {
			private FileSystemNode fsn = null;
			
			@Override
			public void onClick(ClickEvent event) {
				String directory = lucenePathBox.getText();
				FileSystemBrowser fsb = new FileSystemBrowser(clDoc, new OnClick<FileSystemNode>() {
					
					@Override
					public void onClick(final FileSystemNode selected) {
						if (selected != null && selected.isDirectory) {
							fsn = selected;
						}
					}
				}, directory);
				PopupManager.showModal("Filesystem", fsb, 
				new OnClick<PopupPanel>() {
					
					@Override
					public void onClick(final PopupPanel pp) {
						if (fsn != null) {
							lucenePathBox.setText(fsn.absolutePath);
							SRV.configurationService.set(clDoc.getSession(), "LUCENE_INDEX_PATH", fsn.absolutePath, null, new DefaultCallback<Void>(clDoc, "") {

								@Override
								public void onSuccess(Void result) {
									SRV.configurationService.setLuceneIndexPath(clDoc.getSession(),fsn.absolutePath, new DefaultCallback<Void>(clDoc, "") {

										@Override
										public void onSuccess(Void result) {
											new MessageBox("Lucene", "Lucene index path set to '" + fsn.absolutePath + "'", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_INFO).show();
										}
									});
									pp.hide();
								}
							});
						}
					}
				}, null);
			}
		});
		
		final TextBox docArchivePathBox = new TextBox(); 
		final HorizontalPanel hpDocArchive = new HorizontalPanel();
		final PushButton pbDocArchive = new PushButton("...");
		pbDocArchive.setPixelSize(24, 24);
		hpDocArchive.setWidth("100%");
		hpDocArchive.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpDocArchive.add(docArchivePathBox);
		hpDocArchive.add(pbDocArchive);
		pbDocArchive.addClickHandler(new ClickHandler() {
			private FileSystemNode fsn = null;
			
			@Override
			public void onClick(ClickEvent event) {
				String directory = docArchivePathBox.getText();
				FileSystemBrowser fsb = new FileSystemBrowser(clDoc, new OnClick<FileSystemNode>() {
					
					@Override
					public void onClick(final FileSystemNode selected) {
						if (selected != null && selected.isDirectory) {
							fsn = selected;
						}
					}
				}, directory);
				PopupManager.showModal("Filesystem", fsb, 
				new OnClick<PopupPanel>() {
					
					@Override
					public void onClick(final PopupPanel pp) {
						if (fsn != null) {
							docArchivePathBox.setText(fsn.absolutePath);
							SRV.configurationService.set(clDoc.getSession(), IDocArchive.DOC_ARCHIVE_PATH, fsn.absolutePath, null, new DefaultCallback<Void>(clDoc, "") {

								@Override
								public void onSuccess(Void result) {
									SRV.configurationService.setDocArchivePath(clDoc.getSession(),fsn.absolutePath, new DefaultCallback<Void>(clDoc, "") {

										@Override
										public void onSuccess(Void result) {
											new MessageBox("DocARchive", "DocArchive path set to '" + fsn.absolutePath + "'", MessageBox.MB_OK, MESSAGE_ICONS.MB_ICON_INFO).show();
										}
									});
									pp.hide();
								}
							});
						}
					}
				}, null);
			}
		});
		
		lucenePathBox.setWidth("100%");
		docArchivePathBox.setWidth("100%");

		
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
				addLabeledWidget("Lucene index path", true, hpLucene);
				addLabeledWidget("DocArchive path", true, hpDocArchive);
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
		form.toDialog();
	}

}

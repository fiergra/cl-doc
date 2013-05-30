package com.ceres.cldoc.client.views;

import java.util.List;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.ClickableTree;
import com.ceres.cldoc.client.controls.ListRetrievalService;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.FileSystemNode;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FileSystemBrowser extends ClickableTree<FileSystemNode> {

	private static String directory;
	
	public FileSystemBrowser(ClDoc clDoc,
			OnClick<FileSystemNode> onClick, String startDir) {
	
		super(clDoc, new ListRetrievalService<FileSystemNode>() {

			@Override
			public void retrieve(String filter, AsyncCallback<List<FileSystemNode>> callback) {
				directory = filter != null ? filter : ".";
				SRV.configurationService.listFiles(directory, callback);
			}

		}, onClick, false);
		setPixelSize(400, 600);
		final Label lbDirectory = new Label();
		addWidget(lbDirectory);
		Image pbUp = addButton("Up", "icons/32/Button-Upload-icon.png");
		pbUp.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				refresh(directory + "/..");
				lbDirectory.setText(directory);
			}
		});
		refresh(startDir);
	}

	@Override
	protected Widget itemRenderer(FileSystemNode er) {
		String name = er.isDirectory ? "<b>" + er.name + "</b>" : er.name;
		String attribs = (er.r ? "r" : "-") + (er.w ? "w" : "-") + (er.x ? "x" : "-");
		return new HTML(name + " [" + attribs + "]");
	}
	
	

}

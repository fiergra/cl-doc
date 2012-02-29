package com.ceres.cldoc.client.views;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopupManager {

	public static PopupPanel showModal(String title, Widget content) {
		return showModal(title, content, false);
	}
	
	public static PopupPanel showModal(String title, Widget content, boolean autoHide) {
		PopupPanel result = null;
		if (autoHide) {
			result = new PopupPanel(autoHide, true);
		} else {
			DialogBox dlg = new DialogBox();
			dlg.setText(title);
			dlg.setGlassEnabled(true);
			result = dlg;
		}
		result.setAnimationEnabled(false);
		result.setTitle(title);
		result.setWidget(content);
		result.center();
		
		return result;
	}
}

package com.ceres.cldoc.client.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class MessageBox extends DialogBox {

	public static interface ResultCallback <T> {
		void callback(T result);
	}
	
//	public void MessageBox(String title, String htmlMessage, MESSAGE_ICONS icon) {
//		MessageBox(title, htmlMessage, MB_OK, icon, new ResultCallback<Integer>() {
//			
//			@Override
//			public void callback(Integer result) {
//			}
//		});
//	}
//	
	public static final int MB_OK = 1;
	public static final int MB_CANCEL = 2;
	public static final int MB_YES = 4;
	public static final int MB_NO = 8;
	public static final int MB_YESNO = MB_YES | MB_NO;
	public static final int MB_YESNOCANCEL = MB_YES | MB_NO | MB_CANCEL;
	
	public static enum MESSAGE_ICONS { MB_ICON_OK, MB_ICON_INFO, MB_ICON_QUESTION, MB_ICON_EXCLAMATION };
	
	public MessageBox(String title, String htmlMessage, int flags, MESSAGE_ICONS icon) {
		super(false, true);
		
		setText(title);
		setGlassEnabled(true);
		
		DockLayoutPanel dlgWidget = new DockLayoutPanel(Unit.PX);
//		VerticalPanel vp = new VerticalPanel();
		dlgWidget.setSize("400px", "150px");
//		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		HTML theMessage = new HTML(htmlMessage);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		HorizontalPanel innerButtonsPanel = new HorizontalPanel();
		innerButtonsPanel.setSpacing(3);
		buttonsPanel.add(innerButtonsPanel);
		
		if ((flags & MB_OK) != 0) {
			addButton(innerButtonsPanel, "Ok", MB_OK);
		}
		if ((flags & MB_YES) != 0) {
			addButton(innerButtonsPanel, "Ja", MB_YES);
		}
		if ((flags & MB_NO) != 0) {
			addButton(innerButtonsPanel, "Nein", MB_NO);
		}
		if ((flags & MB_CANCEL) != 0) {
			addButton(innerButtonsPanel, "Abbruch", MB_CANCEL);
		}
		
		String imageSource = null;
		
		if (icon == MESSAGE_ICONS.MB_ICON_INFO) {
			imageSource = "icons/48/Button-Info-icon.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_OK) {
			imageSource = "icons/48/Button-Info-icon.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_QUESTION) {
			imageSource = "icons/48/Button-Help-icon.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_EXCLAMATION) {
			imageSource = "icons/48/Button-Warning-icon.png";
		}
		
		if (imageSource != null) {
			HorizontalPanel imagePanel = new HorizontalPanel();
			imagePanel.setPixelSize(48, 48);
			imagePanel.setStylePrimaryName("imagePanel");
			
			Image image = new Image(imageSource);
			image.setPixelSize(48, 48);
			
			imagePanel.add(image);
			dlgWidget.addWest(imagePanel, 64);
		}
		
		dlgWidget.addSouth(buttonsPanel, 32);
		dlgWidget.add(theMessage);
		
		setWidget(dlgWidget);
		center();
		
	}
	
	private void addButton(HorizontalPanel buttonsPanel, String label, final int result) {

		Button pbOk = new Button(label);
		pbOk.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent arg0) {
				hide(result);
			}});
		
		pbOk.setWidth("80px");
		buttonsPanel.add(pbOk);
		
	}

	private void hide(int result) {
		hide();
		onClick(result);
	}
	
	protected void onClick(int result) {}

}

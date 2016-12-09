package com.ceres.dynamicforms.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class MessageBox {

	public static final int MB_OK = 1;
	public static final int MB_CANCEL = 2;
	public static final int MB_YES = 4;
	public static final int MB_NO = 8;
	public static final int MB_YESNO = MB_YES | MB_NO;
	
	public static enum MESSAGE_ICONS { MB_ICON_OK, MB_ICON_INFO, MB_ICON_QUESTION, MB_ICON_ERROR, MB_ICON_WARNING };

	public static void show(String title, String htmlMessage, int flags, MESSAGE_ICONS icon,  final ResultCallback<Integer> callback) {
		show(title, new HTML(htmlMessage), flags, icon, callback, 400, 150);
	}
	
	public static void show(String title, Widget content, int flags, MESSAGE_ICONS icon,  final ResultCallback<Integer> callback, int width, int height) {
		final DialogBox dlg = new DialogBox(false, true);
		
		dlg.setText(title);
		dlg.setGlassEnabled(true);
		
		DockLayoutPanel dlgWidget = new DockLayoutPanel(Unit.PX);
		dlgWidget.setSize(width + "px", height + "px");
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		HorizontalPanel innerButtonsPanel = new HorizontalPanel();
		innerButtonsPanel.setSpacing(3);
		buttonsPanel.add(innerButtonsPanel);
		
		if ((flags & MB_OK) != 0) {
			addButton(dlg, innerButtonsPanel, "Ok", MB_OK, callback, true);
		}
		
		if ((flags & MB_CANCEL) != 0) {
			addButton(dlg, innerButtonsPanel, getLabel("Cancel"), MB_CANCEL, callback);
		}
		if ((flags & MB_YES) != 0) {
			addButton(dlg, innerButtonsPanel, getLabel("Yes"), MB_YES, callback, true);
		}
		if ((flags & MB_NO) != 0) {
			addButton(dlg, innerButtonsPanel, getLabel("No"), MB_NO, callback);
		}
		
		String imageSource = null;
		
		if (icon == MESSAGE_ICONS.MB_ICON_INFO) {
			imageSource = "images/info.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_OK) {
			imageSource = "images/info.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_QUESTION) {
			imageSource = "images/question.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_WARNING) {
			imageSource = "images/warning.png";
		} else if (icon == MESSAGE_ICONS.MB_ICON_ERROR) {
			imageSource = "images/error.png";
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
		dlgWidget.add(content);
		
		dlg.setWidget(dlgWidget);
		dlg.center();
		
//		final HandlerRegistration handler = Event.addNativePreviewHandler(new NativePreviewHandler() {
//
//			@Override
//			public void onPreviewNativeEvent(NativePreviewEvent event) {
//				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
//					event.consume();
//					button.click();
//				}
//			}
//		});
//		
//		dlg.addCloseHandler(new CloseHandler<PopupPanel>() {
//			
//			@Override
//			public void onClose(CloseEvent<PopupPanel> arg0) {
//				handler.removeHandler();
//			}
//		});

	}

	private static String getLabel(String string) {
		return string;
	}

	private static void addButton(final DialogBox dlg, HorizontalPanel buttonsPanel, String label, final int result, final ResultCallback<Integer> callback) {
		addButton(dlg, buttonsPanel, label, result, callback, false);
	}
	
	private static void addButton(final DialogBox dlg, HorizontalPanel buttonsPanel, String label, final int result, final ResultCallback<Integer> callback, boolean focus) {

		final Button button = new Button(label);
		button.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent arg0) {
				hide(dlg, result, callback);
			}});
		
		button.setWidth("80px");
		buttonsPanel.add(button);
		
		if (focus) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				
				@Override
				public void execute() {
					button.setFocus(true);
				}
			});

		}
	}

	private static void hide(DialogBox dlg, int result, ResultCallback<Integer> callback) {
		dlg.hide();
		callback.callback(result);
	}
	

	
}

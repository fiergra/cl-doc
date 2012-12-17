package com.ceres.cldoc.client.views;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.ceres.cldoc.client.service.SRV;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupManager {

	public static PopupPanel showModal(String title, IsWidget content) {
		return showModal(title, content, false);
	}
	
	public static PopupPanel showModal(String title, IsWidget content, boolean autoHide) {
		PopupPanel result = createPopup(title, content, autoHide);
		result.center();
		return result;
	}
	
	private static PopupPanel createPopup(String title, IsWidget content, boolean autoHide) {
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
	
		return result;
	}
	
	public static <T> PopupPanel showModal(String title, IsWidget content, OnClick<PopupPanel> onClickSave, OnClick<PopupPanel> onClickDelete) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(content);
		vp.add(new HTML("<hr width=\"100%\">"));
		final PopupPanel popup = PopupManager.createPopup(title, vp, false);
		HorizontalPanel buttons = addButtons(popup, onClickSave, onClickDelete);//, onClickCancel);
		vp.add(buttons);
		
		int height = RootLayoutPanel.get().getOffsetHeight() - 120;
		int width = RootLayoutPanel.get().getOffsetWidth() - 80;
		content.asWidget().setPixelSize(width, height);
		
		popup.center();
		return popup;
	}

	private static <T> HorizontalPanel addButtons(final PopupPanel popup, final OnClick<PopupPanel> onClickSave,
			final OnClick<PopupPanel> onClickDelete) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		if (onClickSave != null) {
			final Button pbOk = new Button(SRV.c.save());
			hp.add(pbOk);
			pbOk.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onClickSave.onClick(popup);
				}
			});
		}

		if (onClickDelete != null) {
			final Button pbCancel = new Button(SRV.c.delete());
			hp.add(pbCancel);
			pbCancel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onClickDelete.onClick(popup);
				}
			});
		}

		final Button pbCancel = new Button(SRV.c.cancel());
		hp.add(pbCancel);
		pbCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
			}
		});

		
		
		return hp;
	}


}

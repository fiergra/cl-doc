package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.service.SRV;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
	
	public static <T> PopupPanel showModal(T model, String title, Widget content, OnClick<T> onClickSave, OnClick<T> onClickDelete, OnClick<T> onClickCancel) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(content);
		vp.add(new HTML("<hr width=\"100%\">"));
		HorizontalPanel buttons = addButtons(model, onClickSave, onClickDelete, onClickCancel);
		vp.add(buttons);

		PopupPanel popup = PopupManager.showModal(title, vp);
		
		return popup;
	}

	private static <T> HorizontalPanel addButtons(final T model, final OnClick<T> onClickSave,
			final OnClick<T> onClickDelete, final OnClick<T> onClickCancel) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(3);

		if (onClickSave != null) {
			final Button pbOk = new Button(SRV.c.save());
			hp.add(pbOk);
//			pbOk.setStylePrimaryName("button");
//			pbOk.addStyleName("gray");
			pbOk.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onClickSave.onClick(model);
				}
			});
		}

		if (onClickCancel != null) {
			final Button pbCancel = new Button(SRV.c.cancel());
			hp.add(pbCancel);
//			pbCancel.setStylePrimaryName("button");
//			pbCancel.addStyleName("gray");
			pbCancel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onClickCancel.onClick(model);
				}
			});
		}

		if (onClickDelete != null) {
			final Button pbCancel = new Button(SRV.c.delete());
			hp.add(pbCancel);
//			pbCancel.setStylePrimaryName("button");
//			pbCancel.addStyleName("gray");
			pbCancel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onClickDelete.onClick(model);
				}
			});
		}

		return hp;
	}


}

package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class GenericItemRenderer extends DockLayoutPanel {

	private HTML title;
	private OnOkHandler<GenericItem> onInsertUpdateDelete;
	private Runnable onSetModified;
	
	public GenericItemRenderer(OnOkHandler<GenericItem> onInsertUpdateDelete, Runnable onSetModified) {
		super(Unit.PX);
		this.onInsertUpdateDelete = onInsertUpdateDelete;
		this.onSetModified = onSetModified;
		setup();
	}

	private Form <GenericItem> form;
	
	private void addLinkButton(HorizontalPanel buttons, int index, String label, String image, 
			ClickHandler clickHandler) {
		Image linkButton = new Image(image);
		linkButton.addStyleName("linkButton");
		buttons.add(linkButton);
		if (clickHandler != null) {
			linkButton.addClickHandler(clickHandler);
		} else {
			// show disabled
		}
	}
	
	
	private void setup() {
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setWidth("100%");
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		HorizontalPanel buttons = new HorizontalPanel();
		titlePanel.setStylePrimaryName("buttonsPanel");
		int index = 0;
//		addLinkButton(buttons, index++, "icons/32/Adobe-PDF-Document-icon.png", null);
//		addLinkButton(buttons, index++, "icons/32/Edit-Document-icon.png", null);
//		addLinkButton(buttons, index++, "icons/32/Button-Reload-icon.png", null);
		addLinkButton(buttons, index++, "save", "icons/32/Save-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveForm(true);
			}
		});
		addLinkButton(buttons, index++, "delete", "icons/32/File-Delete-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SRV.valueBagService.delete(form.model, new DefaultCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						setValueBag(null);
						onInsertUpdateDelete.onOk(null);
					}
				});
			}
		});

		
		HorizontalPanel textPanel = new HorizontalPanel();
		textPanel.setWidth("100%");
		title = new HTML();
		textPanel.add(title);

		titlePanel.add(textPanel);
		title.setStylePrimaryName("valueBagTitle");
		titlePanel.add(buttons);

		addNorth(titlePanel, 36);
	}

	private void saveForm(final boolean doSelect) {
		form.fromDialog();
		SRV.valueBagService.save(form.model, new DefaultCallback<GenericItem>() {

			@Override
			public void onSuccess(GenericItem item) {
				onInsertUpdateDelete.onOk(doSelect ? item : null);
			}
		});
		
	}
	
	public boolean setValueBag(GenericItem vb) {
		if (form != null) {
			remove(form);
		}

		if (vb != null) {
			title.setHTML(vb.getId() + " - <b>" + vb.getClassName() + "</b>");
			if (form != null) {
				if (form.isModified() && wantToSave()) {
					saveForm(false);
				}
			}
			form = getValueBagRenderer(vb);
			add(form);
		} else {
			title.setText("");
		}
		
		return true;
	}


	private boolean wantToSave() {
		return true;
	}


	private Form<GenericItem> getValueBagRenderer(final GenericItem vb) {
		final Form<GenericItem> form = new Form<GenericItem>(vb, new Runnable() {
			
			@Override
			public void run() {
				title.setHTML("*<i>" + title.getText() + "</i>");
			}
		}){

			@Override
			protected void setup() {};
		};		
	
		if (vb.getClassName().equals("externalDoc")) {
			Frame frame = new Frame(vb.getString("url"));
			frame.setSize("100%", "100%");
			form.setSize("100%", "100%");
			form.setWidget(0, 0, frame);
		} else {
			String layoutDefinition = vb.getLayoutDefinition();
			
			if (layoutDefinition != null) {
				form.parseAndCreate(layoutDefinition);
				form.setWidth("100%");
			} else {
				form.addLine("error", "no layout definition available", Form.DataTypes.FT_STRING, null);
			}
		}		
		return form;
	}
}

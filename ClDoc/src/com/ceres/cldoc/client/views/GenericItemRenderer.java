package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.IGenericItemField;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class GenericItemRenderer extends DockLayoutPanel {

	private static final int BORDER_WIDTH = 3;
	private HTML title;
	private OnOkHandler<GenericItem> onInsertUpdateDelete;
	private Runnable onSetModified;
	private ClDoc clDoc;
	
	public GenericItemRenderer(
			ClDoc clDoc,
			OnOkHandler<GenericItem> onInsertUpdateDelete, 
			Runnable onSetModified) {
		super(Unit.PX);
		this.clDoc = clDoc;
		this.onInsertUpdateDelete = onInsertUpdateDelete;
		this.onSetModified = onSetModified;
		setup();
	}

	private Widget formContainer = new SimplePanel();
	private Form <GenericItem> formContent;
	
	private void addLinkButton(HorizontalPanel buttons, int index, String label, String image, 
			ClickHandler clickHandler) {
		Image linkButton = new Image(image);
		linkButton.addStyleName("linkButton");
		linkButton.setTitle(label);
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
		buttons.setSpacing(3);
		titlePanel.setStylePrimaryName("buttonsPanel");
		int index = 0;
		addLinkButton(buttons, index++, SRV.c.print(), "icons/32/Adobe-PDF-Document-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
//				print(item);
			}
		});
//		addLinkButton(buttons, index++, "icons/32/Edit-Document-icon.png", null);
//		addLinkButton(buttons, index++, "icons/32/Button-Reload-icon.png", null);
		addLinkButton(buttons, index++, SRV.c.save(), "icons/32/Save-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveForm(true);
			}
		});
		addLinkButton(buttons, index++, SRV.c.delete(), "icons/32/File-Delete-icon.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new MessageBox("Loeschen", "Wollen Sie das Dokument entgueltig loeschen?", MessageBox.MB_YES | MessageBox.MB_NO, MessageBox.MESSAGE_ICONS.MB_ICON_QUESTION){

					@Override
					protected void onClick(int result) {
						if (result == MessageBox.MB_YES) {
							SRV.valueBagService.delete(clDoc.getSession(), formContent.model, new DefaultCallback<Void>(clDoc, "delete") {
	
								@Override
								public void onSuccess(Void result) {
									setValueBag(null, null);
									onInsertUpdateDelete.onOk(null);
								}
							});
						}
					}};
			}
		});

		
		HorizontalPanel textPanel = new HorizontalPanel();
		textPanel.setWidth("100%");
		title = new HTML();
		textPanel.add(title);

		titlePanel.add(textPanel);
		title.setStylePrimaryName("valueBagTitle");
		titlePanel.add(buttons);

		addNorth(titlePanel, 38);
		formContainer.setWidth("100%");
		add(formContainer);
		
		addStyleName("formContainer");
	}

	protected void print(GenericItem item) {
//		SRV.valueBagService.print(item, new DefaultCallback<String>() {
//
//			@Override
//			public void onSuccess(String result) {
//				DialogBox dlg = new DialogBox(true, true);
//				
//				Frame content = new Frame(result);
//				content.setSize("800px", "600px");
//				dlg.setWidget(content);
//				dlg.setText(SRV.c.print());
//				dlg.setGlassEnabled(true);
//				dlg.setAnimationEnabled(true);
//				dlg.center();
//			}
//		});
		
		
	}


	private void saveForm(final boolean doSelect) {
		formContent.fromDialog();
		SRV.valueBagService.save(clDoc.getSession(), formContent.model, new DefaultCallback<GenericItem>(clDoc, "listCatalogs") {

			@Override
			public void onSuccess(GenericItem item) {
				onInsertUpdateDelete.onOk(doSelect ? item : null);
			}
		});
		
	}
	
	public boolean setValueBag(LayoutDefinition layoutDef, GenericItem item) {
		int h;
		int w; 
//		item = vb;

		if (formContainer != null) {
			h = formContainer.getOffsetHeight();
			w = formContainer.getOffsetWidth(); 
			remove(formContainer);
		} else {
			h = getOffsetHeight() - 2 * BORDER_WIDTH;
			w = getOffsetWidth() - 2 * BORDER_WIDTH; 
		}

		if (item != null) {
			DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yyyy");
			title.setTitle("#" + item.id + " - <b>" + item.className + "</b>");
			String sDate = item.date != null ? formatter.format(item.date) : "--.--.----";
			title.setHTML("<b>" + item.className + "</b> - " + sDate );
			if (formContent != null) {
				if (formContent.isModified() && wantToSave()) {
					saveForm(false);
				}
			}
			
			formContent = getValueBagRenderer(layoutDef, item, w, h);
			if (item.className.equals("externalDoc")) {
				formContainer = formContent;
			} else {
				HorizontalPanel hp = new HorizontalPanel();
				hp.add(formContent);
				formContainer = hp;
			}	
			formContainer.setWidth("100%");
			add(formContainer);
		} else {
			title.setText("");
		}
		
		return true;
	}


	private boolean wantToSave() {
		return true;
	}


	private Form<GenericItem> getValueBagRenderer(LayoutDefinition layoutDef, final GenericItem vb, int w, int h) {
		final Form<GenericItem> form = new Form<GenericItem>(clDoc, vb, new Runnable() {
			
			@Override
			public void run() {
				title.setHTML("*<i>" + title.getText() + "</i>");
			}
		}){

			@Override
			protected void setup() {};
		};		
	
		if (vb.className.equals("externalDoc")) {
			IGenericItemField field = vb.get("file");
			String baseUrl = GWT.getModuleBaseURL();
			Frame frame = new Frame(baseUrl + "download?id=" + field.getId());
			Widget center = getCenter();
			frame.setPixelSize(w, h);
			frame.setWidth("100%");
			form.setSize("100%", "100%");
			form.setWidget(0, 0, frame);
		} else {
			String layoutDefinition = layoutDef.xmlLayout;
			
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

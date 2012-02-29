package com.ceres.cldoc.client.views;

import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.IGenericItemField;
import com.ceres.cldoc.model.LayoutDefinition;
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

public class GenericItemRenderer extends DockLayoutPanel {

	private HTML title;
	private OnOkHandler<GenericItem> onInsertUpdateDelete;
	private Runnable onSetModified;
	private Session session;
	
	public GenericItemRenderer(
			Session session,
			OnOkHandler<GenericItem> onInsertUpdateDelete, 
			Runnable onSetModified) {
		super(Unit.PX);
		this.session = session;
		this.onInsertUpdateDelete = onInsertUpdateDelete;
		this.onSetModified = onSetModified;
		setup();
	}

	private HorizontalPanel formContainer;
	private Form <GenericItem> formContent;
	private GenericItem item;
	
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
				print(item);
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
				SRV.valueBagService.delete(session, formContent.model, new DefaultCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						setValueBag(null, null);
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

		addNorth(titlePanel, 38);
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
		SRV.valueBagService.save(session, formContent.model, new DefaultCallback<GenericItem>() {

			@Override
			public void onSuccess(GenericItem item) {
				onInsertUpdateDelete.onOk(doSelect ? item : null);
			}
		});
		
	}
	
	public boolean setValueBag(LayoutDefinition layoutDef, GenericItem vb) {
		item = vb;
		if (formContainer != null) {
			remove(formContainer);
		}

		if (vb != null) {
			title.setHTML("#" + vb.id);// + " - <b>" + vb.getSummary() + "</b>");
			if (formContent != null) {
				if (formContent.isModified() && wantToSave()) {
					saveForm(false);
				}
			}
			formContent = getValueBagRenderer(layoutDef, vb);
			formContainer = new HorizontalPanel();
			formContainer.add(formContent);
			add(formContainer);
			
		} else {
			title.setText("");
		}
		
		return true;
	}


	private boolean wantToSave() {
		return true;
	}


	private Form<GenericItem> getValueBagRenderer(LayoutDefinition layoutDef, final GenericItem vb) {
		final Form<GenericItem> form = new Form<GenericItem>(session, vb, new Runnable() {
			
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
			Frame frame = new Frame("/cldoc/download?id=" + field.getId());
			frame.setSize("100%", "100%");
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

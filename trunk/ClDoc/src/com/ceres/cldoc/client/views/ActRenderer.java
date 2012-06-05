package com.ceres.cldoc.client.views;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.LayoutDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ActRenderer extends DockLayoutPanel {

	private static final int BORDER_WIDTH = 3;
	private HTML title;
	private OnOkHandler<Act> onInsertUpdateDelete;
	private ClDoc clDoc;
	
	public ActRenderer(
			ClDoc clDoc,
			OnOkHandler<Act> onInsertUpdateDelete, 
			Runnable onSetModified) {
		super(Unit.PX);
		this.clDoc = clDoc;
		this.onInsertUpdateDelete = onInsertUpdateDelete;
		setup();
	}

	private Widget formContainer = new SimplePanel();
	private IView<Act> formContent;
	private Act act;
	
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
//		addLinkButton(buttons, index++, SRV.c.print(), "icons/32/Adobe-PDF-Document-icon.png", new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				print(formContent.getModel());
//			}
//		});
		
		final Anchor a = new Anchor("<img src=\"icons/32/Adobe-PDF-Document-icon.png\"/>", true);
		a.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=pdf&id=" + act.id , "_blank", "");
//				a.setHref("/cldoc/download?type=pdf&id=" + act.id);
				
//				DialogBox dlg = new DialogBox(true, true);
//				dlg.setTitle("asdf");
//
//				Frame content = new Frame("cldoc/download?type=pdf&id=" + act.id);
//				content.setSize("800px", "600px");
//				dlg.setWidget(content);
//				dlg.setText(SRV.c.print());
//				dlg.setGlassEnabled(true);
//				dlg.setAnimationEnabled(true);
//				dlg.center();
				
			}
		});
		buttons.add(a);
		
		
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
							SRV.actService.delete(clDoc.getSession(), formContent.getModel(), new DefaultCallback<Void>(clDoc, "delete") {
	
								@Override
								public void onSuccess(Void result) {
									setAct(null, null);
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
		titlePanel.setStylePrimaryName("actTitle");
		titlePanel.add(buttons);

		addNorth(titlePanel, 38);
		formContainer.setWidth("100%");
		add(formContainer);
		
		addStyleName("formContainer");
	}

	protected void print(Act act) {
		SRV.actService.print(clDoc.getSession(), act, new DefaultCallback<String>(clDoc, "print") {

			@Override
			public void onSuccess(String result) {
				DialogBox dlg = new DialogBox(true, true);
				
				Frame content = new Frame(result);
				content.setSize("800px", "600px");
				dlg.setWidget(content);
				dlg.setText(SRV.c.print());
				dlg.setGlassEnabled(true);
				dlg.setAnimationEnabled(true);
				dlg.center();
			}
		});
		
		
	}


	private void saveForm(final boolean doSelect) {
		formContent.fromDialog();
		SRV.actService.save(clDoc.getSession(), formContent.getModel(), new DefaultCallback<Act>(clDoc, "listCatalogs") {

			@Override
			public void onSuccess(Act act) {
				formContent.clearModification();
				onInsertUpdateDelete.onOk(doSelect ? act : null);
			}
		});
		
	}
	
	public boolean setAct(LayoutDefinition layoutDef, Act act) {
		int h;
		int w; 

		this.act = act;
		
		if (formContainer != null) {
			h = formContainer.getOffsetHeight();
			w = formContainer.getOffsetWidth(); 
			remove(formContainer);
		} else {
			h = getOffsetHeight() - 2 * BORDER_WIDTH;
			w = getOffsetWidth() - 2 * BORDER_WIDTH; 
		}

		if (act != null) {
			DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yyyy");
			title.setTitle("#" + act.id + " - <b>" + act.className + "</b>");
			String sDate = act.date != null ? formatter.format(act.date) : "--.--.----";
			title.setHTML("<b>" + act.className + "</b> - " + sDate );
			if (formContent != null) {
				if (formContent.isModified() && wantToSave()) {
					saveForm(false);
				}
			}
			
			formContent = getActRenderer(layoutDef, act, w, h);
			if (act.className.equals("externalDoc")) {
				formContainer = formContent.asWidget();
			} else {
				ScrollPanel sp = new ScrollPanel(formContent.asWidget());
				formContainer = sp;
			}	
			formContainer.setWidth("100%");
			add(formContainer);
		} else {
			title.setText("");
		}
		
		return true;
	}


	private boolean wantToSave() {
//		new MessageBox("Speichern", "Wollen Sie die Aenderungen speichern?", MessageBox.MB_YES | MessageBox.MB_NO, MESSAGE_ICONS.MB_ICON_QUESTION).center();
		return true;
	}


//	private Form<GenericAct> getActRenderer(LayoutDefinition layoutDef, final GenericAct vb, int w, int h) {
//		final Form<GenericAct> form = new Form<GenericAct>(clDoc, vb, new Runnable() {
//			
//			@Override
//			public void run() {
//				title.setHTML("*<i>" + title.getText() + "</i>");
//			}
//		}){
//
//			@Override
//			protected void setup() {};
//		};		
//	
//		if (vb.className.equals("externalDoc")) {
//			IGenericActField field = vb.get("file");
//			String baseUrl = GWT.getModuleBaseURL();
//			Frame frame = new Frame(baseUrl + "download?id=" + field.getId());
//			Widget center = getCenter();
//			frame.setPixelSize(w, h);
//			frame.setWidth("100%");
//			form.setSize("100%", "100%");
//			form.setWidget(0, 0, frame);
//		} else {
//			String layoutDefinition = layoutDef.xmlLayout;
//			
//			if (layoutDefinition != null) {
//				form.parseAndCreate(layoutDefinition);
//				form.setWidth("100%");
//			} else {
//				form.addLine("error", "no layout definition available", Form.DataTypes.FT_STRING, null);
//			}
//		}		
//		return form;
//	}
	
	private Form<Act> getActRenderer(LayoutDefinition layoutDef, final Act vb, int w, int h) {
		final Form<Act> form = new Form<Act>(clDoc, vb, new Runnable() {
			
			@Override
			public void run() {
				title.setHTML("*<i>" + title.getText() + "</i>");
			}
		}){

			@Override
			protected void setup() {};
		};		
	
		if (vb.className.equals("externalDoc")) {
			IActField field = vb.get("docId");
			String baseUrl = GWT.getModuleBaseURL();
			Frame frame = new Frame(baseUrl + "download?id=" + field.getLongValue());
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

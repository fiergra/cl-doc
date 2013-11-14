package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.client.views.dynamicforms.CatalogMultiSelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.CatalogSingleSelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.HumanBeingSelectorFactory;
import com.ceres.cldoc.client.views.dynamicforms.IActRenderer;
import com.ceres.cldoc.client.views.dynamicforms.PagesFactory;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.WidgetCreator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ActRenderer2 extends LayoutPanel implements IActRenderer {

	public static final int SAVE_INSERT = 0;
	public static final int SAVE_UPDATE = 1;
	public static final int SAVE_DELETE = 2;
	
	private static final int BORDER_WIDTH = 3;
	private HTML title;
	private final OnOkHandler<Integer> onInsertUpdateDelete;
	private LinkButton pbSave;
	private final ClDoc clDoc;

	static {
		WidgetCreator.addLinkFactory("pages", new PagesFactory());
		WidgetCreator.addLinkFactory("humanbeing", new HumanBeingSelectorFactory());
		WidgetCreator.addLinkFactory("list", new CatalogSingleSelectorFactory(true));
		WidgetCreator.addLinkFactory("option", new CatalogSingleSelectorFactory(false));
		WidgetCreator.addLinkFactory("multiselect", new CatalogMultiSelectorFactory());
	}
	
	public ActRenderer2(
			ClDoc clDoc,
			OnOkHandler<Integer> onInsertUpdateDelete, 
			Runnable onSetModified) {
		super();
		this.clDoc = clDoc;
		this.onInsertUpdateDelete = onInsertUpdateDelete;
		setup();
	}

	private Interactor interactor = new Interactor();
	private Act act;
	private Widget renderer = null;
	
	private final Collection<LinkButton> actButtons = new ArrayList<LinkButton>();
	
	private LinkButton addLinkButton(HorizontalPanel buttons, int index, String toolTip, String enabledImage, String disabledImage, 
			ClickHandler clickHandler) {
		LinkButton linkButton = new LinkButton(toolTip, enabledImage, disabledImage, clickHandler);
		buttons.add(linkButton);
		return linkButton;
	}
	
	
	private void setup() {
		addStyleName("docform");
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setWidth("100%");
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		titlePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(3);
		titlePanel.setStylePrimaryName("buttonsPanel");
		

		int index = 1;
		LinkButton pbPrint = addLinkButton(buttons, index++, SRV.c.print(), "icons/32/Adobe-PDF-Document-icon.png", "icons/32/Adobe-PDF-Document-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String baseUrl = GWT.getModuleBaseURL();
				Window.open(baseUrl + "download?type=pdf&id=" + act.id , "_blank", "");
			}
		});
		pbPrint.enable(false);
		actButtons.add(pbPrint);
		buttons.add(pbPrint);

		pbSave = addLinkButton(buttons, index++, SRV.c.save(), "icons/32/Save-icon.png", "icons/32/Save-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveForm(null);
			}
		});
		pbSave.enable(false);

		LinkButton pbDelete = addLinkButton(buttons, index++, SRV.c.delete(), "icons/32/File-Delete-icon.png", "icons/32/File-Delete-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new MessageBox("Loeschen", "Wollen Sie das Dokument entgueltig loeschen?", MessageBox.MB_YES | MessageBox.MB_NO, MessageBox.MESSAGE_ICONS.MB_ICON_QUESTION){

					@Override
					protected void onClick(int result) {
						if (result == MessageBox.MB_YES) {
							act.isDeleted = true;
							SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "delete") {
	
								@Override
								public void onSuccess(Act result) {
									setAct(null, null);
									if (onInsertUpdateDelete != null) {
										onInsertUpdateDelete.onOk(SAVE_DELETE);
									}
								}
							});
						}
					}};
			}
		});
		pbDelete.enable(false);
		actButtons.add(pbDelete);
			
		buttons.add(new HTML("<vr width=\"15\" height=\"32\" />"));
		LinkButton pbAttachments = addLinkButton(buttons, index++, SRV.c.attachments(), "icons/32/Document-Attach-icon.png", "icons/32/Document-Attach-icon.disabled.png", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				PopupManager.showModal(SRV.c.attachments(), new AttachmentsPanel(clDoc, act), null, null);
			}
		});
		pbAttachments.enable(false);
		actButtons.add(pbAttachments);
		
		HorizontalPanel textPanel = new HorizontalPanel();
		textPanel.setWidth("100%");
		title = new HTML();
		title.addStyleName("actTitle");
		textPanel.add(title);

		titlePanel.add(textPanel);
		titlePanel.setStylePrimaryName("actTitle");
		titlePanel.add(buttons);

		add(titlePanel);
		setWidgetHorizontalPosition(titlePanel, Alignment.BEGIN);
		setWidgetTopHeight(titlePanel, 0, Unit.PCT, 3, Unit.EM);
		addStyleName("formContainer");
		
	}


	private void saveForm(final Runnable callback) {
		interactor.fromDialog(act);
		final boolean isNew = act.id == null;
		SRV.actService.save(clDoc.getSession(), act, new DefaultCallback<Act>(clDoc, "listCatalogs") {

			@Override
			public void onSuccess(Act act) {
				interactor.toDialog(act);
				pbSave.enable(false);
				setTitle(act);
				if (onInsertUpdateDelete != null) {
					onInsertUpdateDelete.onOk(isNew ? SAVE_INSERT : SAVE_UPDATE);
				}
				if (callback != null) {
					callback.run();
				}
			}
		});
		
	}

	@Override
	public boolean setAct(final LayoutDefinition layoutDef, final Act act) {
		if (interactor.isModified()) {
			new MessageBox("Speichern",
					"Wollen Sie die Aenderungen speichern?", MessageBox.MB_YES
							| MessageBox.MB_NO | MessageBox.MB_CANCEL,
					MESSAGE_ICONS.MB_ICON_QUESTION) {

				@Override
				protected void onClick(int result) {
					switch (result) {
					case MessageBox.MB_YES:
						saveForm(new Runnable() {

							@Override
							public void run() {
								doSetAct(layoutDef, act);
							}
						});
						break;
					case MessageBox.MB_NO:
						doSetAct(layoutDef, act);
						break;
					case MessageBox.MB_CANCEL:
						break;
					}
				}

			}.center();
		} else {
			doSetAct(layoutDef, act);
		}
		enableActButtons(act != null);

		return true;
	}

	private void enableActButtons(boolean enabled) {
		for (LinkButton w:actButtons) {
			w.enable(enabled);
		}
	}


	private boolean doSetAct(LayoutDefinition layoutDef, final Act act) {

		if (renderer != null) {
			remove(renderer);
		}
		
		if (act != null) {
			if (act.actClass.name.equals(ActClass.EXTERNAL_DOC.name)) {
				IActField field = act.get("docId");
				String baseUrl = GWT.getModuleBaseURL();
				FrameView frame = new FrameView(act, baseUrl + "download?id=" + field.getLongValue());
				int h = getOffsetHeight() - 2 * BORDER_WIDTH;
				int w = getOffsetWidth() - 2 * BORDER_WIDTH; 
				frame.setPixelSize(w, h);
				frame.setWidth("100%");
				renderer = frame;
				frame.setSize("100%", "100%");
			} else {
				interactor = new Interactor();
				interactor.setChangeHandler(new Runnable() {
					
					@Override
					public void run() {
						String titleText = getTitleText(act);
						if (interactor.isModified()) {
							title.setHTML("*<i>" + titleText + "</i>");
						} else {
							title.setHTML(titleText);
						}
						pbSave.enable(interactor.isModified() && interactor.isValid());
					}
				});
				renderer = WidgetCreator.createWidget(clDoc, layoutDef.xmlLayout, interactor);
			}
			add(renderer);
			interactor.toDialog(act);
			setWidgetTopHeight(renderer, 3, Unit.EM, 100, Unit.PCT);
			this.act = act;
			setTitle(act);
		}			
		return true;
	}

	private String getTitleText(Act act) {
		DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yyyy");
		String sDate = act.date != null ? formatter.format(act.date) : "--.--.----";
		String sResult = "<b>" + act.actClass.name + "</b> - " + sDate;
		return sResult;
	}
	
	private void setTitle(Act act) {
		title.setTitle("#" + act.id + " - <b>" + act.actClass.name + "</b>");
		title.setHTML(getTitleText(act));
	}

	public Act getAct() {
		return act;
	}


}

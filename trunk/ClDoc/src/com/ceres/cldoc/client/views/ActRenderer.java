package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.Collection;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.LinkButton;
import com.ceres.cldoc.client.controls.PagesView;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.client.views.MessageBox.MESSAGE_ICONS;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class ActRenderer extends LayoutPanel {

	private static final int BORDER_WIDTH = 3;
	private HTML title;
	private final OnOkHandler<Act> onInsertUpdateDelete;
	private LinkButton pbSave;
//	private Label imgValid;
	private final ClDoc clDoc;
	
	public ActRenderer(
			ClDoc clDoc,
			OnOkHandler<Act> onInsertUpdateDelete, 
			Runnable onSetModified) {
		super();
		this.clDoc = clDoc;
		this.onInsertUpdateDelete = onInsertUpdateDelete;
		setup();
	}

	private ParticipationEditor participationEditor;
	final CheckBox cbParticipationEditor = new CheckBox("");
	
	private IForm formContent;
	private Act act;
	private LayoutDefinition layoutDefinition;
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
		
		buttons.add(cbParticipationEditor);
		cbParticipationEditor.setValue(true);
		cbParticipationEditor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				participationEditor.setVisible(cbParticipationEditor.getValue());
			}
		});
		
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
				saveForm(true, null);
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
							SRV.actService.delete(clDoc.getSession(), (Act) formContent.getModel(), new DefaultCallback<Void>(clDoc, "delete") {
	
								@Override
								public void onSuccess(Void result) {
									setAct(null, null);
									if (onInsertUpdateDelete != null) {
										onInsertUpdateDelete.onOk(null);
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
				PopupManager.showModal(SRV.c.attachments(), new AttachmentsPanel(clDoc, (Act) formContent.getModel()), null, null);
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
		
		participationEditor = new ParticipationEditor(clDoc, Participation.ADMINISTRATOR);
		participationEditor.setStyleName("participationEditor");
		participationEditor.setVisible(false);
		add(participationEditor);
		setWidgetRightWidth(participationEditor, 0, Unit.PX, 160, Unit.PX);
		setWidgetTopHeight(participationEditor, 2, Unit.EM, 160, Unit.PX);

	}


	private void saveForm(final boolean doSelect, final Runnable callback) {
		formContent.fromDialog();
		SRV.actService.save(clDoc.getSession(), (Act) formContent.getModel(), new DefaultCallback<Act>(clDoc, "listCatalogs") {

			@Override
			public void onSuccess(Act act) {
				formContent.setModel(act);
				formContent.toDialog();
				formContent.clearModification();
				pbSave.enable(false);
				setTitle(act);
				if (onInsertUpdateDelete != null) {
					onInsertUpdateDelete.onOk(doSelect ? act : null);
				}
				if (callback != null) {
					callback.run();
				}
			}
		});
		
	}
	
	public boolean setAct(final LayoutDefinition layoutDef, final Act act) {
		this.layoutDefinition = layoutDef;
		if (act != null) {
			if (formContent != null && formContent.isModified()) {
				new MessageBox("Speichern", "Wollen Sie die Aenderungen speichern?", MessageBox.MB_YES | MessageBox.MB_NO | MessageBox.MB_CANCEL, MESSAGE_ICONS.MB_ICON_QUESTION){

					@Override
					protected void onClick(int result) {
						switch(result) {
							case MessageBox.MB_YES:
								saveForm(false, new Runnable() {
									
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
		} 
		enableActButtons(act != null);
		participationEditor.setVisible(cbParticipationEditor.getValue() && act != null);

		return true;
	}


	private void enableActButtons(boolean enabled) {
		for (LinkButton w:actButtons) {
			w.enable(enabled);
		}
	}


	private boolean doSetAct(LayoutDefinition layoutDef, Act act) {

		if (formContent != null) {
			remove(formContent);
		}
		
		if (act.actClass.name.equals(ActClass.EXTERNAL_DOC.name)) {
			IActField field = act.get("docId");
			String baseUrl = GWT.getModuleBaseURL();
			FrameView frame = new FrameView(act, baseUrl + "download?id=" + field.getLongValue());
			int h = getOffsetHeight() - 2 * BORDER_WIDTH;
			int w = getOffsetWidth() - 2 * BORDER_WIDTH; 
			frame.setPixelSize(w, h);
			frame.setWidth("100%");
			formContent = frame;
			frame.setSize("100%", "100%");
		} else {
			formContent = getActRenderer(clDoc, layoutDef.xmlLayout, act, new Runnable() {
				
				@Override
				public void run() {
					title.setHTML("*<i>" + title.getText() + "</i>");
					pbSave.enable(formContent.isModified() && formContent.isValid());
				}
			}, new Runnable() {
				
				@Override
				public void run() {
					pbSave.enable(formContent.isModified() && formContent.isValid());
				}
			});
		}
		add(formContent);
		participationEditor.setAct(formContent.getInteractor());
		participationEditor.setVisible(true);

		formContent.toDialog();
		setWidgetTopHeight(formContent, 3, Unit.EM, 100, Unit.PCT);
		this.act = act;
		setTitle(act);
		
		return true;
	}

	
	
	private void setTitle(Act act) {
		DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yyyy");
		title.setTitle("#" + act.id + " - <b>" + act.actClass.name + "</b>");
		String sDate = act.date != null ? formatter.format(act.date) : "--.--.----";
		title.setHTML("<b>" + act.actClass.name + "</b> - " + sDate );
	}


	public static IForm getActRenderer(ClDoc clDoc, String xml, final Act act, Runnable onChange, Runnable onValidate) {
		IForm result = null;
		
		if (xml != null) {
			
			Document document = XMLParser.parse(xml);
			NodeList childNodes = document.getChildNodes();
			
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				
				if (item.getNodeName().equals("pages")) {
					PagesView pages = new PagesView(act);
					NodeList subItems = item.getChildNodes();
					for (int j = 0; j < subItems.getLength(); j++) {
						if (subItems.item(j) instanceof Element) {
							Element subItem = (Element) subItems.item(j);
							
							if (subItem.getNodeName().equals("form")) {
								Form form = new Form(clDoc, act, onChange, onValidate);
								form.createAndLayout(subItem);
								form.setWidth("100%");
								pages.addPage(form, subItem.getAttribute("label"));
							}
						}
					}
					// todo: add scrollpanel
					result = pages;
					pages.setSize("100%", "100%");
				} else if (item.getNodeName().equals("form")) {
					Form form = new Form(clDoc, act, onChange, onValidate);
					form.createAndLayout(item);
					form.setWidth("100%");
					result = new ScrollView(form);
				}
				
			}
			
		}
		
		return result;
	}


	public Act getAct() {
		return act;
	}


	public void resetAct(Act act) {
		setAct(layoutDefinition, act);
	}
	
}

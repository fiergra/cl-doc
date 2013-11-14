package com.ceres.cldoc.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.IAct;
import com.ceres.core.IApplication;
import com.ceres.dynamicforms.client.components.DateTextBox;
import com.ceres.dynamicforms.client.components.FloatTextBox;
import com.ceres.dynamicforms.client.components.LongTextBox;
import com.ceres.dynamicforms.client.components.TimeTextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;


public class Form extends FlexTable implements IForm {

	public enum DataType {
		FT_STRING, FT_TEXT, FT_DATE, FT_TIME, FT_PARTICIPATION_TIME, FT_ACTDATE, FT_INTEGER, FT_FLOAT, FT_LIST_SELECTION, FT_OPTION_SELECTION, FT_MULTI_SELECTION, FT_BOOLEAN, FT_PARTICIPATION, FT_HUMANBEING, FT_UNDEF, FT_IMAGE, FT_SEPARATOR, FT_YESNO
	};

	protected IAct model;
	protected DateTimeFormat df = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);
	private final IApplication clDoc;
	private final ClInteractor interactor;

	final static int OK = 1;
	final static int CLOSE = 2;
	final static int CANCEL = 4;
	private static final int SPACING = 3;

	private class FormItemValidationStatus implements ValidationStatus {

		private final Image status;

		public FormItemValidationStatus(Image status) {
			super();
			this.status = status;
		}



		@Override
		public void set(States state) {
			switch (state) {
			case none: 
				status.setUrl("icons/16/valid.png"); 
				status.setHeight("0px");
				status.setVisible(true);
				break;
			case required: 
				status.setVisible(true);
				status.setUrl("icons/16/star_red.png"); break;
			case valid: 
				status.setVisible(true);
				status.setUrl("icons/16/valid.png"); break;
			}
		}	
	}
	
	public Form(IApplication clDoc, IAct model, Runnable setModified, final Runnable setValid) {
		this.clDoc = clDoc;
		addStyleName("docform");
		setRowFormatter(new RowFormatter() {});
		getColumnFormatter().setWidth(1, "16px");
		getColumnFormatter().setWidth(2, "100%");
		this.model = model;
		ValidationCallback validationCallback = new ValidationCallback() {
			
			@Override
			public void setValid(ClInteractorLink link, boolean isValid) {
				if (setValid != null) {
					setValid.run();
				}
				
			}
		};

		interactor = new ClInteractor(clDoc.getSession(), model, setModified, validationCallback );
		setup();
		interactor.toDialog();
	}

	protected IApplication getClDoc() {
		return clDoc;
	}
	
	@Override
	public void setModel(IAct model) {
		this.model = model;
	}
	
//	private static class InteractorLink {
//		public String name;
//		public Widget widget;
//		public DataType dataType;
//		public boolean isMandatory;
//
//		public InteractorLink(String name, Widget widget, DataType dataType, boolean isMandatory) {
//			super();
//			this.name = name;
//			this.widget = widget;
//			this.dataType = dataType;
//			this.isMandatory = isMandatory;
//		}
//
//	}
//

	private PopupPanel popup;
	private int row;


	protected void setup() {}

	private HorizontalPanel addButtons(final OnClick onClickSave,
			final OnClick onClickDelete, final OnClick onClickCancel) {
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
					fromDialog();
					onClickSave.onClick(model);
					popup.hide();
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
					popup.hide();
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
					popup.hide();
				}
			});
		}

		return hp;
	}

	public void showModal(String title, OnClick onClickSave, OnClick onClickDelete, OnClick onClickCancel) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(this);
		vp.add(new HTML("<hr width=\"100%\">"));
		HorizontalPanel buttons = addButtons(onClickSave, onClickDelete, onClickCancel);
		vp.add(buttons);
		setWidth("800px");

		popup = PopupManager.showModal(title, vp);
	}

	public void close() {
		if (popup != null) {
			popup.hide();
		}
	}

	protected Image addLabeledWidget(String label, boolean required, IsWidget... widgets) {
		Image img = null;
		
		if (label == null && widgets.length == 1) {
			setWidget(row, 0, widgets[0]);
			getFlexCellFormatter().setColSpan(row, 0, 3);
		} else {
			Label l = new Label(label);
			setWidget(row, 0, l);
			l.addStyleName("formLabel");
			if (required) {
				l.addStyleName("requiredLabel");
			}

			IsWidget w;
			img = new Image();
			img.setPixelSize(16,  16);
			img.setVisible(false);
			setWidget(row, 1, img);
			
			if (widgets.length > 1) {
				HorizontalPanel hp = new HorizontalPanel();
				hp.setSpacing(SPACING);
				for (IsWidget widget : widgets) {
					hp.add(widget);
				}
				w = hp;
			} else {
				w = widgets[0];
			}
			setWidget(row, 2, w);
			
			getCellFormatter().addStyleName(row, 0, "formItem");		
			getCellFormatter().addStyleName(row, 1, "formItem");		
		}
		row++;
		return img;
	}
	
	
/*	
	protected Image addLabeledWidget(String label, boolean required, IsWidget... widgets) {
		Image img = null;
		
		if (label == null && widgets.length == 1) {
			setWidget(row, 0, widgets[0]);
			getFlexCellFormatter().setColSpan(row, 0, 2);
		} else {
			Label l = new Label(label);
			setWidget(row, 0, l);
			l.addStyleName("formLabel");
			if (required) {
				l.addStyleName("requiredLabel");
			}
			getFlexCellFormatter().setColSpan(row, 0, 2);
			row++;
			IsWidget w;
			img = new Image();
			img.setPixelSize(16,  16);
			img.setVisible(false);
			setWidget(row, 0, img);
			getCellFormatter().addStyleName(row, 0, "itemStatusIcon");		
			
			if (widgets.length > 1) {
				HorizontalPanel hp = new HorizontalPanel();
				hp.setSpacing(SPACING);
				for (IsWidget widget : widgets) {
					hp.add(widget);
				}
				w = hp;
			} else {
				w = widgets[0];
			}
			setWidget(row, 1, w);
			getCellFormatter().addStyleName(row, 1, "formItem");		
			
			getFlexCellFormatter().setAlignment(row, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		}
		row++;
		return img;
	}
*/
	protected Widget addLine(String labelText, String fieldName, DataType dataType, int width, boolean focused) {
		Widget w = addLine(labelText, fieldName, dataType, width);
		if (w instanceof Focusable) {
			((Focusable)w).setFocus(focused);
		}
		return w;
	}

	protected Widget addLine(String labelText, String fieldName,
			DataType dataType, int width) {
		Widget w = addWidgetAndField(labelText, fieldName, dataType, null);
		w.setWidth(width + "em");
		return w;
	}

	protected Widget addLine(String labelText, String fieldName, DataType dataType) {
		return addWidgetAndField(labelText, fieldName, dataType, null);
	}

	protected Widget addLine(String labelText, String fieldName, DataType dataType, HashMap<String, String> attributes) {
		return addWidgetAndField(labelText, fieldName, dataType, attributes);
	}

	private Widget addWidgetAndField(String labelText, String fieldName,
			DataType dataType, HashMap <String, String> attributes) {
		boolean required = getBoolean("required", attributes);
		Widget widget = createWidgetForType(dataType, required, attributes);
		final Image status = addLabeledWidget(labelText, required, widget);
		if (fieldName != null) {
			interactor.addLink(fieldName, new ClInteractorLink(interactor, new FormItemValidationStatus(status), fieldName, widget, dataType, required, attributes));
		}
		return widget;
	}
	

	private boolean getBoolean(String name, HashMap<String, String> attributes) {
		return attributes != null ? "true".equals(attributes.get(name)) : false;
	}
	
	protected void addWidgetsAndFields(String label, List<LineDef> lineDefs) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(5);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.addStyleName("formSubLine");
		Image status = new Image();
		hp.add(status);
		
		for (LineDef ld:lineDefs) {
			DataType dataType = ld.dataType;
			String subLabel = ld.label;
			String subName = ld.fieldName;
			subLabel = subLabel == null ? subName : subLabel;
			
			if (subLabel != null && subLabel.length() > 0) {
				Label l = new Label(subLabel);
				l.addStyleName("formSubLabel");
				hp.add(l);
			}
			boolean required = getBoolean("required", ld.attributes);
			Widget w = createWidgetForType(dataType, required, ld.attributes);
			hp.add(w);
			interactor.addLink(subName, new ClInteractorLink(interactor, new FormItemValidationStatus(status), subName, w, dataType, getBoolean("mandatory", ld.attributes), ld.attributes));
		}
		addLabeledWidget(label, false, hp);
	}
	

	private Widget createWidgetForType(DataType dataType, boolean required, HashMap<String, String> attributes) {
		Widget widget = null;

		switch (dataType) {
		case FT_STRING:
			TextBox t = new TextBox();
			widget = t;
			widget.setWidth("10em");
			break;
		case FT_YESNO:
			YesNoRadioGroup yng = new YesNoRadioGroup();
			widget = yng;
			break;
		case FT_BOOLEAN:
			CheckBox c = new CheckBox();
			widget = c;
			break;
		case FT_LIST_SELECTION:
			CatalogListBox clb = new CatalogListBox(clDoc, attributes.get("parent"));
			widget = clb;
			widget.setWidth("60%");
			break;
		case FT_OPTION_SELECTION:
			CatalogRadioGroup crg = new CatalogRadioGroup(clDoc, attributes.get("parent"), attributes.get("orientation"));
			widget = crg;
			break;
		case FT_MULTI_SELECTION:
			CatalogMultiSelect cms = new CatalogMultiSelect(clDoc, attributes.get("parent"), getMaxCol(attributes), attributes.get("orientation"));
			widget = cms;
			break;
		case FT_PARTICIPATION:
			HumanBeingListBox hlb = new HumanBeingListBox(clDoc, attributes.get("role"));
			widget = hlb;
			widget.setWidth("60%");
			break;
		case FT_HUMANBEING:
			hlb = new HumanBeingListBox(clDoc, attributes.get("role"));
			widget = hlb;
			widget.setWidth("60%");
			break;
		case FT_TEXT:
			TextArea a = new TextArea();
			a.setWidth("99%");
			widget = a;
			break;
		case FT_ACTDATE:
		case FT_DATE:
			DateTextBox d = new DateTextBox();
			d.setWidth("10em");
			widget = d;
			break;
		case FT_PARTICIPATION_TIME:	
			TimeTextBox ptimeBox = new TimeTextBox();
			ptimeBox.setWidth("10em");
			widget = ptimeBox;
			break;
		case FT_TIME:
			TimeTextBox timeBox = new TimeTextBox();
			timeBox.setWidth("10em");
			widget = timeBox;
			break;
		case FT_FLOAT:
			FloatTextBox f = new FloatTextBox();
			f.setWidth("5em");
			widget = f;
			break;
		case FT_INTEGER:
			LongTextBox itb = new LongTextBox();
			itb.setWidth("5em");
			widget = itb;
			break;
		case FT_IMAGE:
			String source = attributes.get("source");
			Image img = new Image("icons/" + source);
			widget = img;
			break;
		case FT_SEPARATOR:
			String title = attributes.get("title");
			String html = "<hr noshade=\"noshade\" size=\"1\"/>";
			if (title != null) {
				html = "</br></br><div class=\"formSectionTitle\"><b>" + title + "</b></div>" + html;
			}
			widget = new HTML(html);
			break;
//		case FT_TIME:
//			TimeTextBox tbx = new TimeTextBox();
//			tbx.setWidth("4em");
//			tbx.addKeyDownHandler(modificationHandler);
//			w = tbx;
//			break;
		default:
			widget = new TextBox();
			break;

		}
		
		if (widget != null) {
			if (attributes != null) {
				String sWidth = attributes.get("width");
				String sHeight = attributes.get("height");
				if (sWidth != null) {
					widget.setWidth(sWidth);
				}
				if (sHeight != null) {
					widget.setHeight(sHeight);
				}
			}
			if (required) {
				widget.addStyleName("required");
			}
		}
		
		return widget;
	}

//	public void parseAndCreate(String xml) {
//		parseAndCreate(xml, true);
//	}
//	
//	protected void parseAndCreate(String xml, final boolean clear) {
//		SRV.configurationService.parse(clDoc.getSession(), xml, new DefaultCallback<LayoutElement>(clDoc, "parse") {
//
//			@Override
//			public void onSuccess(LayoutElement result) {
//				if (result.getChildren() != null && !result.getChildren().isEmpty()) {
//					if (clear) {
//						clear();
//					}
//					createAndLayout(result.getChildren().get(0));
//					toDialog();
//				}
//			}
//		});
//	}
//
//	public static List<Form> parse(ClDoc clDoc, IAct model, Runnable setModified, LayoutElement result) {
//		List<Form> forms = new ArrayList<Form>();
//		
//		if (result.getChildren() != null && !result.getChildren().isEmpty()) {
//			LayoutElement layoutElement = result.getChildren().get(0);
//			
//			if (layoutElement.getType().equals("pages")) {
//				for (LayoutElement child : layoutElement.getChildren()) {
//					forms.add(newForm(clDoc, model, setModified, child));
//				}			
//			} else {
//				forms.add(newForm(clDoc, model, setModified, layoutElement));
//			}
//		}
//		
//		return forms;
//	}
//
//	private static Form newForm(ClDoc clDoc, IAct model, Runnable setModified,
//			LayoutElement layoutElement) {
//		Form f = new Form<IAct>(clDoc, model, setModified) {
//
//			@Override
//			protected void setup() {
//				
//			}
//		};
//		f.createAndLayout(layoutElement);
//		f.toDialog();
//		
//		return f;
//	}
//
	

	private int getMaxCol(HashMap<String, String> attributes) {
		String sMax = attributes.get("columns");
		return sMax != null ? Integer.valueOf(sMax) : 6;
	}

//	public void parseAndCreate(String xml) {
//		parseAndCreate(xml, true);
//	}
//	
//	
//	public void parseAndCreate(String xml, boolean clear) {
//		if (clear) {
//			clear();
//		}
//		
//		Document document = XMLParser.parse(xml);
//		NodeList childNodes = document.getChildNodes();
//		
//		for (int i = 0; i < childNodes.getLength(); i++) {
//			Node item = childNodes.item(i);
//			
//			if (item.getNodeName().equals("form")) {
//				createAndLayout(item);
//			} else if (item.getNodeName().equals("summary")) {
//			    summary = ((Element)item).getNodeValue();
//			}
//		}
//	}
//	
	
	protected void createAndLayout(Node layoutElement) {
		NodeList children = layoutElement.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			Element child = item instanceof Element ? (Element)item : null;
			
			if (child != null) {
				if (child.getNodeName().equals("line")) {
					String label = child.getAttribute("label");
					String fieldName = child.getAttribute("name");
					
					if (child.getChildNodes().getLength() == 0) {
						addWidgetAndField(label == null ? fieldName : label, fieldName, getDataType(child.getAttribute("type")), getAttributesMap(child));//child.getAttributes());
					} else {
						List<LineDef> lineDefs = new ArrayList<LineDef>();
						
						NodeList subChildren = child.getChildNodes();
						for (int j=0; j < subChildren.getLength(); j++) {
							Element sub = (Element) (subChildren.item(j) instanceof Element ? subChildren.item(j) : null);
							if (sub != null) {
								DataType dataType = getDataType(sub);
								String subLabel = sub.getAttribute("label");
								String subName = sub.getAttribute("name");
								subLabel = subLabel == null ? subName : subLabel;
								HashMap<String, String> subAttributes = getAttributesMap(sub);
								
								lineDefs.add(new LineDef(subLabel, subName, dataType, subAttributes));
							}
						}
						addWidgetsAndFields(label, lineDefs);
					}
				}
			}
		}		
	}

	
	private HashMap<String, String> getAttributesMap(Element child) {
		HashMap<String, String> result = new HashMap<String, String>();
		
		NamedNodeMap attributes = child.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			result.put(item.getNodeName(), item.getNodeValue());
		}
		
		return result;
	}

//	protected void createAndLayout(LayoutElement layoutElement) {
//		if (layoutElement.getType().equals("pages")) {
//			TabLayoutPanel pageContainer = getPageContainer();
//			for (LayoutElement child : layoutElement.getChildren()) {
//				Form page = new Form(clDoc, model, new Runnable() {
//					
//					@Override
//					public void run() {
//						if (!isModified) {
//							setModified.run();
//							isModified = true;
//						}
//					}
//				})
//				{
//
//					@Override
//					protected void setup() {
//						setWidth("100%");
//					}
//
//				};
//				pageContainer.add(page, child.getAttribute("label"));
//				page.createAndLayout(child);
//				pages.add(page);
//			} 
//		} else {
//			ArrayList<LayoutElement> children = layoutElement.getChildren();
//			for (LayoutElement child : children) {
//				if (child.getType().equals("form")) {
//					
//				} else if (child.getType().equals("line")) {
//					String label = child.getAttribute("label");
//					String fieldName = child.getAttribute("name");
//					
//					if (child.getChildren().isEmpty()) {
//						addLine(label == null ? fieldName : label, fieldName, getDataType(child), child.getAttributes());
//					} else {
//						HorizontalPanel hp = new HorizontalPanel();
//						hp.setSpacing(5);
//						hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
//						for (LayoutElement sub:child.getChildren()) {
//							DataTypes dataType = getDataType(sub);
//							String subLabel = sub.getAttribute("label");
//							String subName = sub.getAttribute("name");
//							Label l = new Label(subLabel == null ? subName : subLabel);
//							l.addStyleName("formSubLabel");
//							Widget w = createWidgetForType(dataType, sub.getAttributes());
//							hp.add(l);
//							hp.add(w);
//							fields.put(subName, new InteractorLink(subName, w, dataType));
//						}
//						addLine(label == null ? fieldName : label, hp);
//					}
//				}
//			}
//		}		
//	}


	private DataType getDataType(Element sub) {
		return getDataType(sub.getAttribute("type"));
	}

	DataType getDataType(String type) {
		DataType result = DataType.FT_UNDEF;
		
		if (type != null) {
			type = type.toLowerCase();
	
			if (type.equals("string")) {
				result = DataType.FT_STRING;
			} else if (type.equals("text")) {
				result = DataType.FT_TEXT;
			} else if (type.equals("image")) {
				result = DataType.FT_IMAGE;
			} else if (type.equals("separator")) {
				result = DataType.FT_SEPARATOR;
			} else if (type.equals("date")) {
				result = DataType.FT_DATE;
			} else if (type.equals("actdate")) {
				result = DataType.FT_ACTDATE;
			} else if (type.equals("integer")) {
				result = DataType.FT_INTEGER;
			} else if (type.equals("float")) {
				result = DataType.FT_FLOAT;
			} else if (type.equals("participationtime")) {
				result = DataType.FT_PARTICIPATION_TIME;
			} else if (type.equals("time")) {
				result = DataType.FT_TIME;
			} else if (type.equals("yesno")) {
				result = DataType.FT_YESNO;
			} else if (type.equals("boolean")) {
				result = DataType.FT_BOOLEAN;
			} else if (type.equals("list")) {
				result = DataType.FT_LIST_SELECTION;
			} else if (type.equals("option")) {
				result = DataType.FT_OPTION_SELECTION;
			} else if (type.equals("multiselect")) {
				result = DataType.FT_MULTI_SELECTION;
			} else if (type.equals("participation")) {
				result = DataType.FT_PARTICIPATION;
			} else if (type.equals("humanbeing") || type.equals("person")) {
				result = DataType.FT_HUMANBEING;
			}
		}		
		
		return result;
	}

	@Override
	public IAct getModel() {
		return model;
	}

	@Override
	public void fromDialog() {
		interactor.fromDialog();
	}

	@Override
	public void toDialog() {
		interactor.toDialog();
	}

	@Override
	public boolean isModified() {
		return interactor.isModified();
	}

	@Override
	public void clearModification() {
		interactor.clearModification();
	}

	@Override
	public boolean isValid() {
		return interactor.isValid();
	}

	@Override
	public ClInteractor getInteractor() {
		return interactor;
	}

}

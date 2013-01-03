package com.ceres.cldoc.client.views;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.client.controls.DateTextBox;
import com.ceres.cldoc.client.controls.FloatTextBox;
import com.ceres.cldoc.client.controls.LongTextBox;
import com.ceres.cldoc.client.controls.OnDemandChangeListener;
import com.ceres.cldoc.client.service.SRV;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.CatalogList;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


public class Form<T extends IAct> extends FlexTable implements IView<T>{

	public enum DataType {
		FT_STRING, FT_TEXT, FT_DATE, FT_ACTDATE, FT_INTEGER, FT_FLOAT, FT_LIST_SELECTION, FT_OPTION_SELECTION, FT_MULTI_SELECTION, FT_BOOLEAN, FT_PARTICIPATION, FT_HUMANBEING, FT_UNDEF, FT_IMAGE, FT_SEPARATOR
	};

	protected T model;
	protected DateTimeFormat df = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);
	private final Runnable setModified;
	private final ClDoc clDoc;

	final static int OK = 1;
	final static int CLOSE = 2;
	final static int CANCEL = 4;
	private static final int SPACING = 3;

	public Form(ClDoc clDoc, T model, Runnable setModified) {
		this.clDoc = clDoc;
		addStyleName("docform");
		setRowFormatter(new RowFormatter() {});
		this.model = model;
		this.setModified = setModified;
		setup();
		toDialog();
	}

	public void setModel(T model) {
		this.model = model;
	}
	
	private static class Field {
		public String name;
		public Widget widget;
		public DataType dataType;
		public boolean isMandatory;

		public Field(String name, Widget widget, DataType dataType, boolean isMandatory) {
			super();
			this.name = name;
			this.widget = widget;
			this.dataType = dataType;
			this.isMandatory = isMandatory;
		}

	}

	private final HashMap<String, Field> fields = new HashMap<String, Field>();
	
	public static boolean validate(TextBoxBase textBox) {
		boolean isValid = false;
		String sValue = textBox.getText();
		if (sValue == null || sValue.length() == 0) {
			textBox.addStyleName("invalidContent");
		} else {
			textBox.removeStyleName("invalidContent");
			isValid = true;
		}

		return isValid;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void toDialog() {
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			final Field field = iter.next().getValue();
			IAct act = model;// getAct(model, field);

			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
				String sValue = act.getString(field.name);
				((TextBoxBase) field.widget).setText(sValue);
				if (field.isMandatory) {
					validate((TextBoxBase) field.widget);
				}
				break;
			case FT_BOOLEAN:
				((CheckBox) field.widget).setValue(act.getBoolean(field.name));
				break;
			case FT_MULTI_SELECTION:
				((CatalogMultiSelect)field.widget).setSelected(act.getCatalogList(field.name));
				break;
			case FT_OPTION_SELECTION:
			case FT_LIST_SELECTION:
				Catalog catalog = act.getCatalog(field.name);
				if (catalog != null) {
					((IEntitySelector<Catalog>)field.widget).setSelected(catalog);
				}
				break;
			case FT_PARTICIPATION:
				IAssignedEntitySelector<Entity> selector = (IAssignedEntitySelector<Entity>)field.widget;
				Participation participation = act.getParticipation(selector.getRole());
				selector.setSelected(participation != null ? participation.entity : null);
				break;
			case FT_HUMANBEING:
				Long id = act.getLong(field.name);
				if (id != null) {
					SRV.humanBeingService.findById(clDoc.getSession(), id, new DefaultCallback<Person>(clDoc, "findById") {

						@Override
						public void onSuccess(Person result) {
							((IEntitySelector<Person>)field.widget).setSelected(result);
						}
					});
					
				}
				break;
			case FT_DATE:
				Date value = act.getDate(field.name);
				if (value != null) {
					((DateTextBox) field.widget).setDate(value);
				}
				break;
			case FT_ACTDATE:
				if (act.getDate() != null) {
					((DateTextBox) field.widget).setDate(act.getDate());
				}
				break;
			case FT_FLOAT:
				((FloatTextBox) field.widget).setFloat(act.getFloat(field.name));
				break;
			case FT_INTEGER:
				((LongTextBox) field.widget).setLong(act.getLong(field.name));
				break;
//			case FT_TIME:
//				Date dateValue = act.getDate(field.name);
//				if (dateValue != null) {
//					((TimeTextBox) field.widget).setTime(dateValue);
//				}
//				break;
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void fromDialog() {
		Iterator<Entry<String, Field>> iter = fields.entrySet().iterator();

		while (iter.hasNext()) {
			Field field = iter.next().getValue();
			String qualifiedFieldName = field.name;
			IAct act = model;// getAct(model, field);

			switch (field.dataType) {
			case FT_TEXT:
			case FT_STRING:
				act.set(qualifiedFieldName,
						((TextBoxBase) field.widget).getText());
				break;
			case FT_BOOLEAN:
				act.set(qualifiedFieldName, ((CheckBox) field.widget).getValue());
				break;
			case FT_MULTI_SELECTION:
				act.set(qualifiedFieldName, ((IEntitySelector<CatalogList>) field.widget).getSelected());
				break;
			case FT_OPTION_SELECTION:
			case FT_LIST_SELECTION:
				Catalog catalog = ((IEntitySelector<Catalog>) field.widget).getSelected(); 
				act.set(qualifiedFieldName, catalog != null ? catalog : null);
				break;
			case FT_PARTICIPATION:
				IAssignedEntitySelector<Person> selector = (IAssignedEntitySelector<Person>) field.widget;
				Person humanBeing = selector.getSelected(); 
				Participation p = act.getParticipation(selector.getRole());
				
				if (p == null || !p.entity.equals(humanBeing)) {
					act.setParticipant(humanBeing, selector.getRole());
				}
				
				break;
			case FT_HUMANBEING:
				humanBeing = ((IEntitySelector<Person>) field.widget).getSelected(); 
				act.set(qualifiedFieldName, humanBeing != null ? humanBeing.id : null);
				break;
			case FT_DATE:
				act.set(qualifiedFieldName,
						((DateTextBox) field.widget).getDate());
				break;
			case FT_ACTDATE:
				act.setDate(((DateTextBox) field.widget).getDate());
				break;
			case FT_FLOAT:
				act.set(qualifiedFieldName,
						((FloatTextBox) field.widget).getFloat());
				break;
			case FT_INTEGER:
				act.set(qualifiedFieldName,
						((LongTextBox) field.widget).getLong());
				break;
//			case FT_TIME:
//				act.set(qualifiedFieldName,
//						((TimeTextBox) field.widget).getDate());
//				break;
			}
		}

	}

	private PopupPanel popup;
	private boolean isModified = false;
	private int row;

	private class WidgetKeyUpHandler implements KeyUpHandler {

		private final IsWidget widget;

		public WidgetKeyUpHandler(IsWidget widget) {
			this.widget = widget;
		}
		
		@Override
		public void onKeyUp(KeyUpEvent event) {
			onModification();
			if (widget instanceof TextBoxBase) {
				validate((TextBoxBase) widget);
//				System.out.print(((TextBoxBase) widget).getText());
			}
			
			
			if (!isModified) {
				isModified = true;
				if (setModified != null) {
					setModified.run();
				}
			}
		}
		
	}
	
//	private final KeyDownHandler modificationHandler = new KeyDownHandler() {
//
//		@Override
//		public void onKeyDown(KeyDownEvent event) {
//			onModification();
//			if (!isModified) {
//				isModified = true;
//				if (setModified != null) {
//					setModified.run();
//				}
//			}
//		}
//	};

	protected void onModification() {}
	
	@Override
	public boolean isModified() {
		return isModified;
	}
	
	@Override
	public void clearModification() {
		isModified = false;
	}

	protected void setup() {}

	private HorizontalPanel addButtons(final OnClick<T> onClickSave,
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

	public void showModal(String title, OnClick<T> onClickSave, OnClick<T> onClickDelete, OnClick<T> onClickCancel) {
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

	protected void addLine(String label, IsWidget... widgets) {
		if (label == null && widgets.length == 1) {
			setWidget(row, 0, widgets[0]);
			getFlexCellFormatter().setColSpan(row, 0, 2);
		} else {
			Label l = new Label(label);
			setWidget(row, 0, l);
			getCellFormatter().addStyleName(row, 0, "formLabel");	
			getFlexCellFormatter().setColSpan(row, 0, 2);
			row++;
			IsWidget w;
	
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
	}

	protected Widget addLine(String labelText, String fieldName, DataType dataType, int width, boolean focused) {
		Widget w = addLine(labelText, fieldName, dataType, width);
		if (w instanceof Focusable) {
			((Focusable)w).setFocus(focused);
		}
		return w;
	}

	protected Widget addLine(String labelText, String fieldName,
			DataType dataType, int width) {
		Widget w = addLine(labelText, fieldName, dataType, null);
//		w.setWidth(width + "em");
		return w;
	}

	protected Widget addLine(String labelText, String fieldName,
			DataType dataType) {
		return addLine(labelText, fieldName, dataType, null);
	}

	protected Widget addLine(String labelText, String fieldName,
			DataType dataType, HashMap <String, String> attributes) {
		Widget widget = createWidgetForType(dataType, attributes);
		addLine(labelText, widget);
		if (fieldName != null) {
			fields.put(fieldName, new Field(fieldName, widget, dataType, attributes != null ? "true".equals(attributes.get("mandatory")) : false));
		}
		return widget;
	}

	protected Widget createWidgetForType(DataType dataType, HashMap<String, String> attributes) {
		Widget w = null;

		switch (dataType) {
		case FT_STRING:
			TextBox t = new TextBox();
			t.addKeyUpHandler(new WidgetKeyUpHandler(t));
			w = t;
			w.setWidth("60%");
			break;
		case FT_BOOLEAN:
			CheckBox c = new CheckBox();
			c.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = c;
			break;
		case FT_LIST_SELECTION:
			CatalogListBox clb = new CatalogListBox(clDoc, attributes.get("parent"));
			clb.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = clb;
			w.setWidth("60%");
			break;
		case FT_OPTION_SELECTION:
			CatalogRadioGroup crg = new CatalogRadioGroup(clDoc, attributes.get("parent"), attributes.get("orientation"));
			crg.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = crg;
			break;
		case FT_MULTI_SELECTION:
			CatalogMultiSelect cms = new CatalogMultiSelect(clDoc, attributes.get("parent"), 8, attributes.get("orientation"));
			cms.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = cms;
			break;
		case FT_PARTICIPATION:
			HumanBeingListBox hlb = new HumanBeingListBox(clDoc, attributes.get("role"), new OnDemandChangeListener<Person>() {
				
				@Override
				public void onChange(Person oldValue, Person newValue) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = hlb;
			w.setWidth("60%");
			break;
		case FT_HUMANBEING:
			hlb = new HumanBeingListBox(clDoc, attributes.get("role"), new OnDemandChangeListener<Person>() {
				
				@Override
				public void onChange(Person oldValue, Person newValue) {
					if (!isModified) {
						isModified = true;
						setModified.run();
					}
				}
			});
			w = hlb;
			w.setWidth("60%");
			break;
		case FT_TEXT:
			TextArea a = new TextArea();
			a.setWidth("99%");
			a.addKeyUpHandler(new WidgetKeyUpHandler(a));
			w = a;
			break;
		case FT_ACTDATE:
		case FT_DATE:
			DateTextBox d = new DateTextBox();
			d.setWidth("10em");
			d.addKeyUpHandler(new WidgetKeyUpHandler(d));
			w = d;
			break;
		case FT_FLOAT:
			FloatTextBox f = new FloatTextBox();
			f.setWidth("5em");
			f.addKeyUpHandler(new WidgetKeyUpHandler(f));
			w = f;
			break;
		case FT_INTEGER:
			LongTextBox itb = new LongTextBox();
			itb.setWidth("5em");
			itb.addKeyUpHandler(new WidgetKeyUpHandler(itb));
			w = itb;
			break;
		case FT_IMAGE:
			String source = attributes.get("source");
			Image img = new Image("icons/" + source);
			w = img;
			break;
		case FT_SEPARATOR:
			HTML separator = new HTML("<hr noshade=\"noshade\" size=\"1\"/>");
			w = separator;
			break;
//		case FT_TIME:
//			TimeTextBox tbx = new TimeTextBox();
//			tbx.setWidth("4em");
//			tbx.addKeyDownHandler(modificationHandler);
//			w = tbx;
//			break;
		default:
			w = new TextBox();
			break;

		}
		
		if (w != null && attributes != null) {
			String sWidth = attributes.get("width");
			String sHeight = attributes.get("height");
			if (sWidth != null) {
				w.setWidth(sWidth);
			}
			if (sHeight != null) {
				w.setHeight(sHeight);
			}
			
		}
		
		return w;
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
	

	public void parseAndCreate(String xml) {
		parseAndCreate(xml, true);
	}
	
	
	public void parseAndCreate(String xml, boolean clear) {
		if (clear) {
			clear();
		}
		
		Document document = XMLParser.parse(xml);
		NodeList childNodes = document.getChildNodes();
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			
			if (item.getNodeName().equals("form")) {
				createAndLayout(item);
			}
		}
	}
	
	
	protected void createAndLayout(Node layoutElement) {
		NodeList children = layoutElement.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++) {
			Node item = children.item(i);
			Element child = item instanceof Element ? (Element)item : null;
			
			if (child != null && child.getNodeName().equals("line")) {
				String label = child.getAttribute("label");
				String fieldName = child.getAttribute("name");
				
				if (child.getChildNodes().getLength() == 0) {
					addLine(label == null ? fieldName : label, fieldName, getDataType(child.getAttribute("type")), getAttributesMap(child));//child.getAttributes());
				} else {
					HorizontalPanel hp = new HorizontalPanel();
					hp.setSpacing(5);
					hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					NodeList subChildren = child.getChildNodes();
					for (int j=0; j < subChildren.getLength(); j++) {
						Element sub = (Element) (subChildren.item(j) instanceof Element ? subChildren.item(j) : null);
						if (sub != null) {
							DataType dataType = getDataType(sub);
							String subLabel = sub.getAttribute("label");
							String subName = sub.getAttribute("name");
							Label l = new Label(subLabel == null ? subName : subLabel);
							l.addStyleName("formSubLabel");
							HashMap<String, String> subAttributes = getAttributesMap(sub);
							Widget w = createWidgetForType(dataType, subAttributes);
							hp.add(l);
							hp.add(w);
							fields.put(subName, new Field(subName, w, dataType, "true".equals(subAttributes.get("mandatory"))));
						}
					}
					addLine(label == null ? fieldName : label, hp);
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
//				Form page = new Form<T>(clDoc, model, new Runnable() {
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
//							fields.put(subName, new Field(subName, w, dataType));
//						}
//						addLine(label == null ? fieldName : label, hp);
//					}
//				}
//			}
//		}		
//	}


	private TabLayoutPanel getPageContainer() {
		TabLayoutPanel pageContainer = (TabLayoutPanel)(getRowCount() == 1 ? getWidget(0, 0) : null);
		if (pageContainer == null) {
			pageContainer = new TabLayoutPanel(2, Unit.EM);
			pageContainer.setHeight("600px");
//			pageContainer.setSize("800px", "600px");
			setWidget(0,  0, pageContainer);
		}
		return pageContainer;
	}

	private DataType getDataType(Element sub) {
		return getDataType(sub.getAttribute("type"));
	}

	private DataType getDataType(String type) {
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
//				} else if (type.equals("time")) {
//				result = DataTypes.FT_TIME;
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
			} else if (type.equals("humanbeing")) {
				result = DataType.FT_HUMANBEING;
			}
		}		
		
		return result;
	}

	@Override
	public T getModel() {
		return model;
	}	

}

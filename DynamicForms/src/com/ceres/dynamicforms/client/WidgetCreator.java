package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ceres.dynamicforms.client.components.DateTextBox;
import com.ceres.dynamicforms.client.components.EnabledHorizontalPanel;
import com.ceres.dynamicforms.client.components.EnabledVerticalPanel;
import com.ceres.dynamicforms.client.components.FloatTextBox;
import com.ceres.dynamicforms.client.components.MapListRenderer;
import com.ceres.dynamicforms.client.components.NumberTextBox;
import com.ceres.dynamicforms.client.components.TimeTextBox;
import com.ceres.dynamicforms.client.components.YesNoRadioGroup;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class WidgetCreator {

	public static boolean isIE() {
		String ua = Window.Navigator.getUserAgent().toLowerCase();
		return ua.contains("msie");
	}


	public static Widget createWidget(String xml, Interactor interactor) {
		return createWidget(xml, interactor, null);
	}	
	
	public static Widget createWidget(String xml, Interactor interactor, ITranslator translator) {
		if (translator == null) {
			translator = new SimpleTranslator();
		}
		Widget result = null;
		Document document = XMLParser.parse(xml);
		Element root = document.getDocumentElement();
		
		if (root != null) {
			NodeList children = document.getChildNodes();//root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				result = processChild(document, children.item(i), null, interactor, 0, translator);
			}
		}
		
		return result;
	}

	private static Widget processChild(Document document, Node item, Widget panel, Interactor interactor, int level, ITranslator translator) {
		Widget widget = null;
		if (item instanceof Element) {
			Element element = (Element)item;
			
			System.out.println("pc " + level + levelPrefix(level) + element.getNodeName());

			element = preprocess(document, element);
		
			widget = createWidgetFromElement(element, interactor, translator);
			if (widget != null) {
				if (panel instanceof Panel){
					((Panel)panel).add(widget);
				} else if (panel instanceof TabLayoutPanel) {
					((TabLayoutPanel)panel).add(widget, widget.getTitle());
				}
				if (element.hasChildNodes() && 
						(widget instanceof Panel || widget instanceof TabLayoutPanel)) {
					NodeList children = element.getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						processChild(document, children.item(i), widget, interactor, level + 1, translator);
					}
				}
			}
		}
		return widget;
	}

	private static String levelPrefix(int level) {
		String prefix = "";
		for (int i = 0; i < level; i++) {
			prefix += "   ";
		}
		return prefix;
	}
	
	

	private static Element preprocess(Document document, Element element) {
		String nodeName = element.getNodeName();
		int index = nodeName.indexOf(":");
		String localName = nodeName.substring(index > 0 ? index + 1 : 0);

		IPreProcessor processor = preprocessors.get(localName);
		
		if (processor != null) {
			element = processor.process(document, element);
		} else {
			if ("MapList".equals(nodeName)) {
				StringBuffer labels = new StringBuffer();
				List<Node> columns = new ArrayList<>();
				NodeList children = element.getChildNodes();
				int col = 0;
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child instanceof Element && ((Element)child).getNodeName().equals("column")) {
						columns.add(child);
						String label = ((Element)child).getAttribute("label");
						labels.append((label == null ? "label" + col : label) + ";");
						XMLParser.removeWhitespace(child);
						String colDef = child.getFirstChild().toString();
						element.setAttribute("colDef" + col, colDef);
						col++;
					}
				}

				Iterator<Node> iter = columns.iterator();
				while (iter.hasNext()) {
					element.removeChild(iter.next());
				}

				element.setAttribute("columns", String.valueOf(col));
				element.setAttribute("labels", labels.toString());
				
			} else if ("line".equals(nodeName)) {
				if (element.hasAttribute("type")) {
					if (element.getAttribute("type") != null) {
						Element newElement = getElementForType(document,
								element.getAttribute("type"));
		
						NamedNodeMap attributes = element.getAttributes();
						for (int i = 0; i < attributes.getLength(); i++) {
							Node a = attributes.item(i);
							newElement.setAttribute(a.getNodeName(), a.getNodeValue());
						}
		
						element.appendChild(newElement);
//						element.setAttribute("type", null);
						if (newElement.hasAttribute("width")) {
							element.setAttribute("width", "100%");
						}
					}
				} else {
					NodeList children = element.getChildNodes();
					Element newElement = document.createElement("HBox");
					for (int i = 0; i < children.getLength(); i++) {
						Node child = children.item(i);
	//					newElement.appendChild(child);
						element.removeChild(child);
					}
					element.appendChild(newElement);
					element.setAttribute("type", null);
				}
			}
		}
		return element;
	}

	private static Element getElementForType(Document document, String typeName) {
		Element newElement;
		
		typeName = typeName.toLowerCase();
		if (typeName.equals("text")) {
			newElement = document.createElement("ItemFieldTextArea");
			newElement.setAttribute("width", "100%");
		} else if (typeName.equals("string")) {
			newElement = document.createElement("ItemFieldTextInput");
		} else if (typeName.equals("boolean")) {
			newElement = document.createElement("ItemFieldCheckBox");
		} else if (typeName.equals("actdate")) {
			newElement = document.createElement("ItemFromDateField");
		} else {
			newElement = document.createElement(typeName);
		}
		
		return newElement;
	}

	private static HashMap<String, IPreProcessor> preprocessors = new HashMap<String, IPreProcessor>();

	public static void addPreProcessor(String localName, IPreProcessor processor) {
		preprocessors.put(localName, processor);
	}
	
	private static HashMap<String, ILinkFactory> linkFactories = new HashMap<String, ILinkFactory>();

	public static void addLinkFactory(String localName, ILinkFactory factory) {
		linkFactories.put(localName, factory);
	}
	
	private static Widget createWidgetFromElement(Element element, Interactor interactor, ITranslator translator) {
		String tagName = element.getTagName();
		return createWidgetFromElementName(tagName, asHashMap(element.getAttributes()), interactor, translator);
	}
	
	public static Widget createWidgetFromElementName(String tagName, HashMap<String, String> attributes, final Interactor interactor, ITranslator translator) {
		int index = tagName.indexOf(":");
		String localName = tagName.substring(index > 0 ? index + 1 : 0);
		Widget widget = null;
		InteractorWidgetLink link = null;
		final String fieldName; 
		
		if (attributes.containsKey("fieldName")) {
			fieldName = attributes.get("fieldName");
		} else if (attributes.containsKey("name")) {
			fieldName = attributes.get("name");
		} else {
			fieldName = null;
		}
		
		if ("VBox".equals(localName)) {
			widget = new EnabledVerticalPanel();
			widget.setStyleName("VBox");
		} else if ("HBox".equals(localName)){
			widget = new EnabledHorizontalPanel();
			widget.setStyleName("HBox");
		} else if ("MapList".equals(localName)){
			String sLabels = attributes.get(MapListRenderer.LABELS);
			String[] labels = sLabels.split(";");
//			final String className = fieldName;//attributes.get(CLASSNAME);
			final MapList ml = new MapList(translator, labels, null);
			setColDefs(ml, attributes);
			widget = ml;
			final InteractorWidgetLink mlLink = new InteractorWidgetLink(interactor, fieldName, widget, attributes) {
				
				@Override
				public void toDialog(Map<String, Serializable> item) {
					@SuppressWarnings("unchecked")
					List<Map<String, Serializable>> acts = (List<Map<String, Serializable>>) item.get(fieldName);
					ml.toDialog(acts );
				}
				
				@Override
				public boolean isEmpty() {
					return false;
				}
				
				@Override
				public void fromDialog(Map<String, Serializable> item) {
					List<Map<String, Serializable>> acts = new ArrayList<>();
					ml.fromDialog(acts );
					item.put(fieldName, (Serializable) acts);
				}
			};
			link = mlLink;
			ml.setChangeHandler(new Runnable() {
				
				@Override
				public void run() {
					interactor.onChange(mlLink);
				}
			});
			
		} else if ("Tab".equals(localName)){
			widget = new TabLayoutPanel(42, Unit.PX);
		} else if ("Form".equals(localName)){
			widget = new SimpleForm();
			if (attributes.containsKey("label")) {
				widget.setTitle(attributes.get("label"));
			}
		} else if ("form".equals(localName)){
			widget = new SimpleForm();
			attributes.put("width", "100%");
			if (attributes.containsKey("label")) {
				widget.setTitle(attributes.get("label"));
			}
		} else if ("ResourceFormItem".equals(localName) || "FormItem".equals(localName)){
			if (translator != null && attributes.containsKey("label")) {
				attributes.put("label", translator.getLabel(attributes.get("label")));
			}
			widget = new SimpleFormItem(attributes);
		} else if ("line".equals(localName)){
			if (!attributes.containsKey("label")) {
				attributes.put("label", fieldName);
			}
			
			if (translator != null) {
				attributes.put("label", translator.getLabel(attributes.get("label")));
			}
			widget = new SimpleFormItem(attributes);
		} else if ("datebox".equals(localName)){
			final DateBox db =  new DateBox();
			link = new DateBoxLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("ItemFieldDateField".equals(localName) || "date".equals(localName)){
			DateTextBox db =  new DateTextBox();
			link = new DateLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("time".equals(localName)){
			TimeTextBox db = new TimeTextBox();
			link = new DateLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("long".equals(localName) || "float".equals(localName)){
			NumberTextBox db = new NumberTextBox();
			link = new NumberLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("float".equals(localName)){
			FloatTextBox db = new FloatTextBox();
			link = new FloatLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("ItemFieldNumberInput".equals(localName)){
			NumberTextBox db = new NumberTextBox();
			link = new NumberLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("ItemFromDateField".equals(localName)){
			DateTextBox db =  new DateTextBox();
			link = new DateFromLink(interactor, "dateFrom", db, attributes);
			widget = db;
		} else if ("ItemFieldCheckBox".equals(localName)){
			widget = new CheckBox();
			link = new BooleanLink(interactor, fieldName, (CheckBox) widget, attributes);
		} else if ("yesno".equals(localName)){
			widget = new YesNoRadioGroup();
			link = new YesNoLink(interactor, fieldName, (YesNoRadioGroup)widget, attributes);
		} else if ("ItemFieldTextInput".equals(localName)){
			widget = new TextBox();
			link = new TextLink(interactor, fieldName, (TextBoxBase) widget, attributes);
		} else if ("ItemFieldTextArea".equals(localName)){
			widget = new TextArea();
			link = new TextLink(interactor, fieldName, (TextBoxBase) widget, attributes);
		} else if ("Label".equals(localName) || "ResourceLabel".equals(localName)){
			widget = new Label(attributes.get("text"));
			widget.addStyleName("formLabel");
		} else if ("Button".equals(localName)){
			String sLabel = attributes.containsKey("label") ? (translator != null ? translator.getLabel(attributes.get("label")) : attributes.get("label")) : "";
			final PushButton pb = new PushButton(sLabel);
			widget = pb;
			link = new PushButtonLink(interactor, fieldName, pb, attributes);
		} else if ("Link".equals(localName) || "link".equals(localName)){
			Anchor a = new Anchor(attributes.get("text"));
			final String uri = attributes.get("uri");
			a.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Window.open(uri, "_blank", "");
				}
			});
			widget = a;
		} else if ("FormHeading".equalsIgnoreCase(localName) || "ResourceFormHeading".equalsIgnoreCase(localName)){
			HorizontalPanel hp = new HorizontalPanel();
			Label l = new Label(attributes.get("label"));
			hp.addStyleName("formHeading");
			l.addStyleName("formHeadingLabel");
			hp.add(l);
			HTML hr = new HTML("<hr/>");
			hp.add(hr);
			widget = hp;
		} else if ("HRule".equals(localName)){
			widget = new HTML("<hr/>");
		} else if ("HTML".equals(localName)){
			final HTML html = new HTML();
			widget = new ScrollPanel(html);
			link = new InteractorWidgetLink(interactor, fieldName, widget, attributes) {
				
				@Override
				public void toDialog(Map<String, Serializable> item) {
					html.setHTML((String) item.get(fieldName));
				}
				
				@Override
				public boolean isEmpty() {
					return false;
				}
				
				@Override
				public void fromDialog(Map<String, Serializable> item) {
				}
			};
		} else {
			ILinkFactory linkFactory = linkFactories.get(localName);
			if (linkFactory != null) {
				link = linkFactory.createLink(interactor, fieldName, attributes);
				if (link != null && attributes != null) {
					link.setObjectType(attributes.get("objectType"));
				}
				widget = link != null ? link.widget : null;
			} else {
				widget = new Label(localName + " not yet supported!");
			}
		}

		if (widget != null) {
			if (attributes.containsKey("label")) {
				widget.setTitle(translator != null ? translator.getLabel(attributes.get("label")) : attributes.get("label") );
			}

			if (attributes.containsKey("width")) {
				widget.setWidth(checkUnit(attributes.get("width")));
			}

			if (attributes.containsKey("background-color")) {
				widget.getElement().getStyle().setBackgroundColor(attributes.get("background-color"));
			}
			
			if (attributes.containsKey("height")) {
				widget.setHeight(checkUnit(attributes.get("height")));
			}
			if (widget instanceof HasEnabled && attributes.containsKey("enabled")) {
				((HasEnabled)widget).setEnabled(asBoolean(attributes.get("enabled")));
			}

//			if (translator != null) {
//				String objectType = attributes.get("objectType");
//				boolean isVisible = translator.isVisible(objectType);
//				boolean isEnabled = translator.isEnabled(objectType);
//				
//				widget.setVisible(isVisible);
//				if (widget instanceof HasEnabled) { 
//					((HasEnabled)widget).setEnabled(isEnabled);
//				}
//			}

			
			if (link != null && link.getName() != null) {
				interactor.addLink(link);
			}
		}
		
		return widget;
	}


	private static void setColDefs(MapList ml,
			HashMap<String, String> attributes) {
		int count = Integer.valueOf(attributes.get("columns")); 
		for (int col = 0; col < count; col++) {
			String colDef = attributes.get("colDef" + col);
			ml.addColDef(colDef);
		}
	}


	private static String checkUnit(String size) {
		return size.endsWith("%") || size.endsWith("px") || size.endsWith("em") ? size : size + "px";
	}

	private static boolean asBoolean(String b) {
		return !"false".equalsIgnoreCase(b);
	}

	private static HashMap<String, String> asHashMap(NamedNodeMap attributes) {
		HashMap<String, String> result = new HashMap<String, String>(attributes.getLength());
		
		for (int i=0; i<attributes.getLength(); i++) {
			Node a = attributes.item(i);
			result.put(a.getNodeName(), a.getNodeValue());
		}
		return result;
	}
}

package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.ceres.core.IApplication;
import com.ceres.dynamicforms.client.components.DateTextBox;
import com.ceres.dynamicforms.client.components.FloatTextBox;
import com.ceres.dynamicforms.client.components.LongTextBox;
import com.ceres.dynamicforms.client.components.TimeTextBox;
import com.ceres.dynamicforms.client.components.YesNoRadioGroup;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
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

public class WidgetCreator {

	@SuppressWarnings("unused")
	public static Widget createWidget(IApplication application, String xml, Interactor interactor) {
		DockLayoutPanel mainPanel = new DockLayoutPanel(Unit.PX);
		Document document = XMLParser.parse(xml);
		Element root = document.getDocumentElement();
		
		if (root != null) {
			NodeList children = document.getChildNodes();//root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				processChild(application, document, children.item(i), mainPanel, interactor, 0);
			}
		}
		
		if (false) {//application.getSession().isActionAllowed("WORKFLOW_TAB", "DON'T CARE", "VIEW")) {
			TabLayoutPanel theTab = new TabLayoutPanel(3, Unit.EM);
			theTab.add(mainPanel, ">Form");
			theTab.add(createEditor(document), ">Layout");
			return theTab;
		} else {
			return mainPanel;
		}
	}

	private static Widget createEditor(Document document) {
		TextArea txtArea = new TextArea();
		txtArea.setText(prettyPrint(document));
		return txtArea;
	}

	private static String prettyPrint(Document document) {
		return document.toString();
	}

	private static void processChild(IApplication application, Document document, Node item, Widget panel, Interactor interactor, int level) {
		if (item instanceof Element) {
			Element element = (Element)item;
			
			System.out.println(levelPrefix(level) + element.getNodeName());

			preprocess(document, element);
		
			Widget widget = createWidgetFromElement(application, element, interactor);
			if (widget != null) {
				if (panel instanceof Panel){
					((Panel)panel).add(widget);
				} else if (panel instanceof TabLayoutPanel) {
					((TabLayoutPanel)panel).add(widget);
				}
				if (element.hasChildNodes() && 
						(widget instanceof Panel || widget instanceof TabLayoutPanel)) {
					NodeList children = element.getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						processChild(application, document, children.item(i), widget, interactor, level + 1);
					}
				}
			}
		}
	}

	private static String levelPrefix(int level) {
		String prefix = "";
		for (int i = 0; i < level; i++) {
			prefix += "   ";
		}
		return prefix;
	}

	private static void preprocess(Document document, Element element) {
		if ("line".equals(element.getNodeName())) {
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
					element.setAttribute("type", null);
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

	private static HashMap<String, ILinkFactory> linkFactories = new HashMap<String, ILinkFactory>();

	public static void addLinkFactory(String localName, ILinkFactory factory) {
		linkFactories.put(localName, factory);
	}
	private static Widget createWidgetFromElement(IApplication application, Element element, Interactor interactor) {
		String tagName = element.getTagName();
		return createWidgetFromElementName(application, tagName, asHashMap(element.getAttributes()), interactor);
	}
	
	public static Widget createWidgetFromElementName(IApplication application, String tagName, HashMap<String, String> attributes, Interactor interactor) {
		int index = tagName.indexOf(":");
		String localName = tagName.substring(index > 0 ? index + 1 : 0);
		Widget widget = null;
		InteractorLink link = null;
		String fieldName = null; 
		
		if (attributes.containsKey("fieldName")) {
			fieldName = attributes.get("fieldName");
		} else if (attributes.containsKey("name")) {
			fieldName = attributes.get("name");
		}
		
		if ("VBox".equals(localName)) {
			widget = new VerticalPanel();
			widget.setStyleName("VBox");
		} else if ("HBox".equals(localName)){
			widget = new HorizontalPanel();
			widget.setStyleName("HBox");
		} else if ("Form".equals(localName)){
			widget = new SimpleForm(application);
			if (attributes.containsKey("label")) {
				widget.setTitle(attributes.get("label"));
			}
		} else if ("form".equals(localName)){
			widget = new SimpleForm(application);
			attributes.put("width", "100%");
			if (attributes.containsKey("label")) {
				widget.setTitle(attributes.get("label"));
			}
		} else if ("ResourceFormItem".equals(localName) || "FormItem".equals(localName)){
			widget = new SimpleFormItem(attributes.get("label"));
		} else if ("line".equals(localName)){
			String label = attributes.containsKey("label") ? attributes.get("label") : fieldName;
			widget = new SimpleFormItem(label);
		} else if ("StatusComboBox".equals(localName)){
			ListBox listBox = new ListBox();
			listBox.setVisibleItemCount(1);
			widget = listBox;
		} else if ("ItemFieldDateField".equals(localName) || "date".equals(localName)){
			DateTextBox db =  new DateTextBox();
			link = new DateLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("time".equals(localName)){
			TimeTextBox db = new TimeTextBox();
			link = new DateLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("long".equals(localName)){
			LongTextBox db = new LongTextBox();
			link = new LongLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("float".equals(localName)){
			FloatTextBox db = new FloatTextBox();
			link = new FloatLink(interactor, fieldName, db, attributes);
			widget = db;
		} else if ("ItemFromDateField".equals(localName)){
			DateTextBox db =  new DateTextBox();
			link = new DateFromLink(interactor, fieldName, db, attributes);
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
		} else if ("HRule".equals(localName)){
			widget = new HTML("<hr/>");
		} else {
			ILinkFactory linkFactory = linkFactories.get(localName);
			if (linkFactory != null) {
				link = linkFactory.createLink(application, interactor, fieldName, attributes);
				widget = link.widget;
			} else {
				widget = new Label(localName + " not yet supported!");
			}
		}

		if (widget != null) {
			if (attributes.containsKey("width")) {
				widget.setWidth(attributes.get("width"));
			}
			if (attributes.containsKey("height")) {
				widget.setWidth(attributes.get("height"));
			}
			
			if (fieldName != null && link != null) {
				interactor.addLink(link);
			}
		}
		
		return widget;
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

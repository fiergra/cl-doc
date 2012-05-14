package com.ceres.cldoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Participation;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class DocServiceImpl implements IDocService {

	private static Logger log = Logger.getLogger("DocService");

	@Override
	public byte[] print(final Session session, final Act act) {
		LayoutDefinition ld = Locator.getLayoutDefinitionService().load(
				session, act.className, LayoutDefinition.PRINT_LAYOUT);
		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PdfWriter writer;
		try {
			writer = PdfWriter.getInstance(document, out);
			document.open();

			if (ld != null) {
				render(session, act, document, writer, ld.xmlLayout);
			} else {
				defaultLayout(session, act, document, writer);
			}
			document.close();
			writer.close();
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void defaultLayout(Session session, Act act, Document document,
			PdfWriter writer) throws DocumentException {
		document.setMargins(72, 72, 108, 180);
		document.add(new Paragraph(act.className, new Font(
				FontFamily.HELVETICA, 36, Font.BOLD)));
		Font boldFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		Font italicFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.ITALIC);

		Iterator<Entry<String, IActField>> iter = act.fields.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<String, IActField> next = iter.next();
			Paragraph paragraph = new Paragraph(next.getKey() + ": ", boldFont);
			document.add(paragraph);
			Paragraph paragraph2 = new Paragraph(next.getValue().toString(),
					italicFont);
			paragraph2.setIndentationLeft(72);
			document.add(paragraph2);
		}
	}

	private void render(Session session, Act act, Document document,
			PdfWriter writer, String template)
			throws ParserConfigurationException, SAXException, IOException,
			DocumentException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = db.parse(new ByteArrayInputStream(
				template.getBytes("UTF-8")));

		traverse(session, act, doc, null, document, writer);

		// XMLInputFactory factory = XMLInputFactory.newInstance();
		// try {
		// XMLStreamReader parser = factory
		// .createXMLStreamReader(new StringReader(template));
		// boolean eod = false;
		//
		// if (parser.getEventType() == XMLStreamConstants.START_DOCUMENT) {
		// while (!eod) {
		// int event = parser.next();
		//
		// if (event == XMLStreamConstants.END_DOCUMENT) {
		// eod = true;
		// } else if (event == XMLStreamConstants.START_DOCUMENT) {
		// } else if (event == XMLStreamConstants.END_ELEMENT) {
		// } else if (event == XMLStreamConstants.START_ELEMENT) {
		// String localName = parser.getLocalName();
		// String text = parser.getText();
		//
		// int attributeCount = parser.getAttributeCount();
		// for (int i = 0; i < attributeCount; i++) {
		// String attribName = parser.getAttributeLocalName(i);
		// String value = parser.getAttributeValue(null,
		// attribName);
		// }
		// }
		// }
		// }
		// parser.close();
		// } catch (XMLStreamException e) {
		// e.printStackTrace();
		// }
	}

	HashMap<String, Object> defaults;

	private void traverse(Session session, Act act, Node domNode,
			Element pdfNode, Document document, PdfWriter writer)
			throws DocumentException {
		int type = domNode.getNodeType();
		switch (type) {
		// handle document nodes
		case Node.DOCUMENT_NODE: {
			traverse(session, act,
					((org.w3c.dom.Document) domNode).getDocumentElement(),
					null, document, writer);
			break;
		}
		// handle element nodes
		case Node.ELEMENT_NODE: {
			String elementName = domNode.getNodeName();
			log.info(elementName);
			NodeList childNodes = domNode.getChildNodes();
			int numChildren = childNodes != null ? childNodes.getLength() : -1;

			if (elementName.equals("paragraph")) {
				Paragraph p;

				if (numChildren == 1
						&& childNodes.item(0).getNodeType() == Node.TEXT_NODE) {
					p = new Paragraph(replaceVars(session, act, childNodes
							.item(0).getNodeValue()),
							getFont(defaults, domNode));
					setParagraphAttributes(p, domNode, defaults);
				} else {
					p = new Paragraph();
					setParagraphAttributes(p, domNode, defaults);
					traverseChildren(session, act, p, document, writer,
							childNodes);
				}
				document.add(p);
			} else if (elementName.equals("pdf")) {
				defaults = initDefaults(domNode);
				traverseChildren(session, act, pdfNode, document, writer,
						childNodes);
			} else if (elementName.equals("phrase")) {
				Font font = getFont(defaults, domNode);
				Phrase phrase = font != null ? new Phrase(replaceVars(session,
						act, childNodes.item(0).getNodeValue()), font)
						: new Phrase(replaceVars(session, act,
								childNodes.item(0).getNodeValue()));
				((Paragraph) pdfNode).add(phrase);
			} else {
				traverseChildren(session, act, pdfNode, document, writer,
						childNodes);
			}
			break;
		}
		case Node.TEXT_NODE: {
			String data = domNode.getNodeValue().trim();
			log.info(data);
		}
		}
	}

	private void setParagraphAttributes(Paragraph p, Node domNode,
			HashMap<String, Object> defaults) {
		if (domNode.hasAttributes()) {
			NamedNodeMap attributes = domNode.getAttributes();

			Float indentationLeft = getFloat(attributes, "indentationLeft");
			Float indentationRight = getFloat(attributes, "indentationLeft");
			Float spacingBefore = getFloat(attributes, "spacingBefore");
			Float spacingAfter = getFloat(attributes, "spacingAfter");
			String alignment = getString(attributes, "align");

			if (alignment != null) {
				p.setAlignment(getAlignment(alignment));
			}
			if (indentationLeft != null) {
				p.setIndentationLeft(indentationLeft);
			}
			if (indentationRight != null) {
				p.setIndentationRight(indentationRight);
			}
			if (spacingBefore != null) {
				p.setSpacingBefore(spacingBefore);
			}
			if (spacingAfter != null) {
				p.setSpacingAfter(spacingAfter);
			}
		}
	}

	private int getAlignment(String alignment) {
		if ("center".equalsIgnoreCase(alignment)) {
			return Paragraph.ALIGN_CENTER;
		}
		if ("justified".equalsIgnoreCase(alignment)) {
			return Paragraph.ALIGN_JUSTIFIED;
		}
		if ("left".equalsIgnoreCase(alignment)) {
			return Paragraph.ALIGN_LEFT;
		}
		if ("right".equalsIgnoreCase(alignment)) {
			return Paragraph.ALIGN_RIGHT;
		}
		return Paragraph.ALIGN_UNDEFINED;
	}

	private void traverseChildren(Session session, Act act, Element pdfNode,
			Document document, PdfWriter writer, NodeList childNodes)
			throws DocumentException {
		if (childNodes != null) {
			int length = childNodes.getLength();
			for (int i = 0; i < length; i++) {
				traverse(session, act, childNodes.item(i), pdfNode, document,
						writer);
			}
		}
	}

	private HashMap<String, Object> initDefaults(Node domNode) {
		defaults = new HashMap<String, Object>();
		defaults.put("fontSize", 12f);
		defaults.put("fontFamily", FontFamily.HELVETICA);
		defaults.put("fontStyle", Font.NORMAL);

		return defaults;
	}

	private Font getFont(HashMap<String, Object> defaults, Node domNode) {
		Font font = null;
		if (domNode.hasAttributes()) {
			NamedNodeMap attributes = domNode.getAttributes();
			String fontFamily = getString(attributes, "fontFamily");
			String fontStyle = getString(attributes, "fontStyle");
			Float fontSize = getFloat(attributes, "fontSize");

			font = new Font(getFontFamily(fontFamily, defaults), getFontSize(
					fontSize, defaults), getFontStyle(fontStyle, defaults));
		}
		return font;
	}

	private int getFontStyle(String fontStyle, HashMap<String, Object> defaults2) {
		if (fontStyle != null) {
			return Font.getStyleValue(fontStyle);
		}
		return (Integer) defaults.get("fontStyle");
	}

	private float getFontSize(Float fontSize, HashMap<String, Object> defaults) {
		return (Float) (fontSize != null ? fontSize : defaults.get("fontSize"));
	}

	private FontFamily getFontFamily(String fontFamily,
			HashMap<String, Object> defaults) {
		if ("TIMES".equalsIgnoreCase(fontFamily)) {
			return FontFamily.TIMES_ROMAN;
		} else if ("HELVETICA".equalsIgnoreCase(fontFamily)) {
			return FontFamily.HELVETICA;
		} else if ("COURIER".equalsIgnoreCase(fontFamily)) {
			return FontFamily.COURIER;
		} else {
			return (FontFamily) defaults.get("fontFamily");
		}
	}

	private String getString(NamedNodeMap attributes, String name) {
		Node node = attributes.getNamedItem(name);
		return node != null ? node.getTextContent() : null;
	}

	private Float getFloat(NamedNodeMap attributes, String name) {
		String sValue = getString(attributes, name);
		return sValue != null ? Float.parseFloat(sValue) : null;
	}

	private String replaceVars(Session session, Act act, String text) {
		int index = text.indexOf('{');

		while (index != -1) {
			String varName = getVarName(text, index);
			String value = getValue(session, act, varName);
			text = text.replace("{" + varName + "}", value);
			index = text.indexOf('{');
		}
		return text;
	}

	private String getValue(Session session, Act act, String varName) {
		Serializable value = null;
		if (varName.startsWith("PATIENT.")) {
			Participation participation = act
					.getParticipation(Participation.PATIENT);
			if (participation != null) {
				value = getEntityProperty(participation.entity,
						varName.substring("PATIENT.".length()));
			}
		} else {
			value = act.getValue(varName);
		}
		return value != null ? value.toString() : "";
	}

	private Serializable getEntityProperty(AbstractEntity entity, String propertyName) {
		Serializable value = null;
//		try {
//			PropertyDescriptor pd = new PropertyDescriptor(propertyName, entity.getClass());
//			Method read = pd.getReadMethod();
//			value = (Serializable)read.invoke(entity, null);
//		} catch (IntrospectionException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		return null;
		
//		if (entity instanceof Person) {
//			Person p = (Person)entity;
//			if (propertyName.equals("firstName")) { return p.firstName; }
//			if (propertyName.equals("lastName")) { return p.lastName; }
//			if (propertyName.equals("dateOfBirth")) { return p.dateOfBirth; }
//		}
//		return null;
//		
		try {
			Class clazz = entity.getClass();
			Field field = clazz.getDeclaredField(propertyName);
			return (Serializable) (field != null ? field.get(entity) : null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private String format(IActField field) {
		if (field.getType() == IActField.FT_STRING) {
			return field.getStringValue();
		}
		return null;
	}

	private String getVarName(String text, int beginIndex) {
		int endIndex = text.indexOf('}');
		String varName = text.substring(beginIndex + 1, endIndex);
		return varName;
	}
}

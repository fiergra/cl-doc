package com.ceres.cldoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.core.ISession;

public class CatalogServiceImpl implements ICatalogService {

	private static Logger log = Logger.getLogger("CatalogService");

	@Override
	public void save(ISession session, final Catalog catalog) {
		save(session, catalog, true);
	}	
	
	private void save(ISession session, final Catalog catalog, final boolean insertIfUpdateFails) {
		Catalog c = Jdbc.doTransactional(session, new ITransactional() {

			@Override
			public Catalog execute(Connection con) throws SQLException {
				if (catalog.id != null || insertIfUpdateFails) {
					String sql = "update Catalog set code = ?, parent = ?, text = ?, shorttext = ?, date = ?, number1=?, number2=?, logical_order=? where ";
					if (catalog.id != null) {
						sql += "id = ?";
					} else {
						sql += "code = ? and parent " + (catalog.parent == null ? "is null" : "= ?");
					}
					
					PreparedStatement s = con.prepareStatement(sql);
					long parentId = catalog.parent != null ? catalog.parent.id : 1000;
					log.info(parentId +"");
					int i = bindVariables(s, catalog);
					if (catalog.id != null) {
						s.setLong(i++, catalog.id);
					} else {
						s.setString(i++, catalog.code);
						if (catalog.parent != null) {
							s.setLong(i++, catalog.parent.id);
						}
					}
					int rows = s.executeUpdate();
					s.close();
					
					if (insertIfUpdateFails) {
						if (rows == 0) {
							catalog.id = null;
							insert(con, catalog);
						} else if (catalog.id == null) {
							s = con.prepareStatement("select id from Catalog where code = ? and parent " + (catalog.parent == null ? "is null" : "= ?"));
							s.setString(1, catalog.code);
							if (catalog.parent != null) {
								s.setLong(2, catalog.parent.id);
							}
							ResultSet rs = s.executeQuery();
							rs.next();
							catalog.id = rs.getLong("id");
							s.close();
						}
					}
					
				} else {
					insert(con, catalog);
				}
				return catalog;
			}

			private void insert(Connection con, final Catalog catalog)
					throws SQLException {
				PreparedStatement s = con
						.prepareStatement(
								"insert into Catalog (code, parent, text, shorttext, date, number1, number2, logical_Order) values (?,?,?,?,?,?,?,?)",
								new String[] { "ID" });
				int i = bindVariables(s, catalog);
				catalog.id = Jdbc.exec(s);
				s.close();
			}

			private int bindVariables(PreparedStatement u, final Catalog catalog)
					throws SQLException {
				int i = 1;
				u.setString(i++, catalog.code);
				if (catalog.parent != null) {
					u.setLong(i++, catalog.parent.id);
				} else {
					u.setNull(i++, Types.INTEGER);
				}
				u.setString(i++, catalog.text);
				u.setString(i++, catalog.shortText);
				if (catalog.date != null) {
					u.setDate(i++, new java.sql.Date(catalog.date.getTime()));
				} else {
					u.setNull(i++, Types.DATE);
				}
				if (catalog.number1 != null) {
					u.setLong(i++, catalog.number1);
				} else {
					u.setNull(i++, Types.INTEGER);
				}
				if (catalog.number2 != null) {
					u.setLong(i++, catalog.number2);
				} else {
					u.setNull(i++, Types.INTEGER);
				}
				if (catalog.logicalOrder != null) {
					u.setLong(i++, catalog.logicalOrder);
				} else {
					u.setNull(i++, Types.INTEGER);
				}
				return i;
			}
		});
		
		if (catalog.hasChildren()) {
			for (Catalog child:catalog.children) {
				save(session, child);
			}
		}
	}

	@Override
	public Catalog load(ISession session, final String code) {
		int lastDot = code.lastIndexOf('.');
		Catalog result = null;
		
		if (lastDot != -1) {
			String localCode = code.substring(lastDot + 1);
			Iterator<Catalog> iter = loadList(session, code.substring(0, lastDot)).iterator();
			
			while (iter.hasNext() && result == null) {
				Catalog next = iter.next();
				
				if (next.code.equals(localCode)) {
					result = next;
				}
				
			}
			
		}
		return result;
	}	
	
	@Override
	public Catalog load(ISession session, final long id) {
		Catalog result = Jdbc.doTransactional(session, new ITransactional() {

			@Override
			public Catalog execute(Connection con) throws SQLException {
				Catalog c = null;
				String sql = "select * from Catalog where id = ?";
				PreparedStatement s = con.prepareStatement(sql);
				s.setLong(1, id);
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					c = fetchCatalog(rs, "");
				}
				rs.close();
				s.close();
				return c;
			}

		});
		return result;
	}

	public static Catalog fetchCatalog(ResultSet rs, String prefix) throws SQLException {
		Catalog c;
		c = new Catalog(rs.getLong(prefix + "id"));
		c.code = rs.getString(prefix + "code");
		c.shortText = rs.getString(prefix + "shorttext");
		c.text = rs.getString(prefix + "text");
		c.date = rs.getDate(prefix + "date");
		if (rs.wasNull()) {
			c.date = null;
		}
		Long parentId = rs.getLong(prefix + "parent");
		c.parent = rs.wasNull() ? null : new Catalog(parentId);
		Long number = rs.getLong(prefix + "number1");
		c.number1 = rs.wasNull() ? null : number;
		number = rs.getLong(prefix + "number2");
		c.number2 = rs.wasNull() ? null : number;
		number = rs.getLong(prefix + "logical_order");
		c.logicalOrder = rs.wasNull() ? null : number;
		return c;
	}

	@Override
	public List<Catalog> loadList(final ISession session,
			final Catalog parent) {
		List<Catalog> result = Jdbc.doTransactional(session,
				new ITransactional() {

					@Override
					public List<Catalog> execute(Connection con)
							throws SQLException {
						List<Catalog> result = new ArrayList<Catalog>();
						String sql = "select * from Catalog where parent ";

						if (parent == null) {
							sql += "is null";
						} else {
							sql += "= ?";
						}
						sql += " order by IFNULL(logical_order, 99), shorttext, text";
						PreparedStatement s = con.prepareStatement(sql);
						if (parent != null) {
							s.setLong(1, parent.id);
						}
						ResultSet rs = s.executeQuery();
						while (rs.next()) {
							Catalog c = fetchCatalog(rs, "");
							c.parent = parent;
							c.children = loadList(session, c);
							result.add(c);
						}
						rs.close();
						s.close();
						return result;
					}
				});
		return result;
	}

	@Override
	public void delete(ISession session, final Catalog catalog) {
		if (catalog.children != null) {
			for (Catalog c: catalog.children) {
				delete(session, c);
			}
		}
		Jdbc.doTransactional(session, new ITransactional() {

			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con
						.prepareStatement("delete from catalog where id = ?");
				s.setLong(1, catalog.id);
				s.executeUpdate();
				s.close();
				return null;
			}
		});
	}

	private Catalog load(ISession session, Connection con, String code)
			throws SQLException {
		Catalog c = null;
		StringTokenizer st = new StringTokenizer(code, ".");

		if (st.countTokens() > 1) {
			Collection<Catalog> children = loadList(session, st.nextToken());
			while (children != null && st.hasMoreTokens()) {
				String token = st.nextToken();
				c = getChild(children, token);
				if (c != null) {
					children = c.children;
				} else {
					log.severe("child '" + token + "' does not exist in catalog '" + code + "'!");
					children = null;
				}
			}
		} else {
			c = doLoad(con, null, code);
		}
		return c;
	}

	private Catalog getChild(Collection<Catalog> children, String code) {
		Catalog child = null;
		if (children != null) {
			Iterator<Catalog> iter = children.iterator();
			while (child == null && iter.hasNext()) {
				Catalog next = iter.next();
				if (next.code.equals(code)) {
					child = next;
				}
			}
		}
		return child;
	}

	private Catalog doLoad(Connection con, Long parentId, String code)
			throws SQLException {
		String sql = "select * from Catalog where code = ? and ";
		if (parentId == null) {
			sql += "parent is null";
		} else {
			sql += "parent = ?";
		}
		sql += " order by logical_order";

		PreparedStatement s = con.prepareStatement(sql);
		s.setString(1, code);
		if (parentId != null) {
			s.setLong(2, parentId);
		}
		Catalog catalog = null;

		ResultSet rs = s.executeQuery();
		if (rs.next()) {
			catalog = fetchCatalog(rs, "");
		}
		rs.close();
		s.close();

		return catalog;
	}

	@Override
	public List<Catalog> loadList(final ISession session,
			final String parentCode) {
		List<Catalog> list = Jdbc.doTransactional(session,
				new ITransactional() {

					@Override
					public Collection<Catalog> execute(Connection con)
							throws SQLException {
						Catalog parent = load(session, con, parentCode);
						if (parentCode != null && parent == null) {
							return null;
						} else {
							return loadList(session, parent);
						}
					}
				});

		return list;
	}

	@Override
	public String exportXML(ISession session, Catalog parent) {
		String xml = null;
		Collection<Catalog> catalogs = loadList(session, parent);
		if (catalogs != null) {
			try {
				DocumentBuilderFactory dbfac = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = dbfac.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				Element root = doc.createElement("catalogs");
				doc.appendChild(root);
//				Comment comment = doc.createComment("catalogs exported by "
//						+ session.getUser().userName + " at " + new Date());
//				root.appendChild(comment);

				Iterator<Catalog> iter = catalogs.iterator();
				while (iter.hasNext()) {
					catalogToXml(doc, root, iter.next());
				}

				TransformerFactory transfac = TransformerFactory.newInstance();
				Transformer trans = transfac.newTransformer();
				trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				trans.setOutputProperty(OutputKeys.INDENT, "yes");

				// create string from xml tree
				StringWriter sw = new StringWriter();
				StreamResult result = new StreamResult(sw);
				DOMSource source = new DOMSource(doc);
				trans.transform(source, result);
				xml = sw.toString();

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
		return xml;
	}

	private void catalogToXml(Document doc, Node parentNode, Catalog catalog) {
		Element catalogNode = doc.createElement("catalog");
		
		if (catalog.id < 1000) {
			catalogNode.setAttribute("id", String.valueOf(catalog.id));
		} else {
			log.fine("omit custom id.");
		}
		catalogNode.setAttribute("code", catalog.code);

		if (catalog.text != null) {
			Element textNode = doc.createElement("text");
			Text text = doc.createTextNode(catalog.text);
			textNode.appendChild(text);
			catalogNode.appendChild(textNode);
		}
		if (catalog.shortText != null) {
			Element shortTextNode = doc.createElement("shorttext");
			Text shortText = doc.createTextNode(catalog.shortText);
			shortTextNode.appendChild(shortText);
			catalogNode.appendChild(shortTextNode);
		}
		if (catalog.date != null) {
			Element dateNode = doc.createElement("date");
			String sDate = DateFormat.getDateTimeInstance()
					.format(catalog.date);
			Text dateText = doc.createTextNode(sDate);
			dateNode.appendChild(dateText);
			catalogNode.appendChild(dateNode);
		}

		if (catalog.number1 != null) {
			Element numberNode = doc.createElement("number1");
			numberNode.appendChild(doc.createTextNode(String.valueOf(catalog.number1)));
			catalogNode.appendChild(numberNode);
		}

		if (catalog.number2 != null) {
			Element numberNode = doc.createElement("number2");
			numberNode.appendChild(doc.createTextNode(String.valueOf(catalog.number2)));
			catalogNode.appendChild(numberNode);
		}

		if (catalog.logicalOrder != null) {
			Element numberNode = doc.createElement("logicalOrder");
			numberNode.appendChild(doc.createTextNode(String.valueOf(catalog.logicalOrder)));
			catalogNode.appendChild(numberNode);
		}

		parentNode.appendChild(catalogNode);

		if (catalog.children != null && !catalog.children.isEmpty()) {
			Element childrenNode = doc.createElement("children");
			catalogNode.appendChild(childrenNode);
			for (Catalog c : catalog.children) {
				catalogToXml(doc, childrenNode, c);
			}
		}
	}

	@Override
	public void importXML(ISession session, InputStream xml) {
		DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		DocumentBuilder db;
		try {
			db = factory.newDocumentBuilder();
			Document doc = db.parse(xml);
			Element document = doc.getDocumentElement();
			NodeList children = document.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeName().equals("catalog")) {
					importCatalog(session, (Element) child, null);
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Element getChildByName(Element node, String childName) {
		Element child = null;
		
		NodeList children = node.getChildNodes();
		int length = children.getLength();
		int index = 0;
		
		while (child == null && index < length) {
			Node curChild = children.item(index++);
			
			if (childName.equals(curChild.getNodeName())) {
				child = (Element) curChild;
			}
			
		}
		
		
		return child;
	}
	
	private String getNodeText(Element catalogNode, String elementName) {
		Element child = getChildByName(catalogNode, elementName); 
		return child != null ? child.getTextContent() : null;
	}
	
	private void importCatalog(ISession session, Element catalogNode, Catalog parent) {
		Catalog catalog = new Catalog();
		catalog.parent = parent;
		catalog.id = getLong(catalogNode.getAttributes(), "id");
		if (catalog.id != null && catalog.id > 1000) {
			catalog.id = null;
		}
		catalog.code = getString(catalogNode.getAttributes(), "code");
		catalog.text = getNodeText(catalogNode, "text");
		catalog.shortText = getNodeText(catalogNode, "shorttext");
		
		Element child = getChildByName(catalogNode, "date"); 
		if (child != null) {
			catalog.date = parseDate(child.getTextContent());
		}
		
		child = getChildByName(catalogNode, "number1"); 
		if (child != null) {
			catalog.number1 = Long.valueOf(child.getTextContent());
		}
		
		child = getChildByName(catalogNode, "number2"); 
		if (child != null) {
			catalog.number2 = Long.valueOf(child.getTextContent());
		}
		
		child = getChildByName(catalogNode, "logicalOrder"); 
		if (child != null) {
			catalog.logicalOrder = Long.valueOf(child.getTextContent());
		}

		save(session, catalog, true);
		log.info("imported " + catalog);
		
		NodeList childNodes = catalogNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeName().equals("children")) {

				NodeList childrenCatalogs = childNode.getChildNodes();
				for (int j = 0; j < childrenCatalogs.getLength(); j++) {
					Node childCatalog = childrenCatalogs.item(j);
					if (childCatalog.getNodeName().equals("catalog")) {
						importCatalog(session, (Element)childCatalog, catalog);
					}
				}
			}
		}
		
	}

	private Date parseDate(String sDate) {
		try {
			return DateFormat.getDateTimeInstance().parse(sDate);
		} catch (ParseException e) {
			log.warning(e.getLocalizedMessage());
			return null;
		}
	}

	private Long getLong(NamedNodeMap attributes, String name) {
		String value = getString(attributes, name);
		return value != null ? Long.parseLong(value) : null;
	}

	private String getString(NamedNodeMap attributes, String name) {
		Node node = attributes.getNamedItem(name);
		return node != null ? node.getTextContent() : null;
	}

}

package com.ceres.cldoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
import org.xml.sax.SAXException;

import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.util.Jdbc;

public class LayoutDefinitionServiceImpl implements ILayoutDefinitionService {

	private static Logger log = Logger.getLogger("LayoutDefinitionService");

	@Override
	public void save(Session session, final LayoutDefinition ld) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				IActService actService = Locator.getActService();
				actService.registerActClass(con, ld.actClass);
				insert(con, ld);
				return null;
			}

			private void insert(Connection con, final LayoutDefinition ld) throws SQLException {
				PreparedStatement u = con.prepareStatement(
						"update LayoutDefinition set valid_to = CURRENT_TIMESTAMP " +
						"where actclassid = ? and typeid = ? and valid_to is null");
				u.setLong(1, ld.actClass.id);
				u.setInt(2, ld.type);
				int rows = u.executeUpdate();
				log.info("closed " + rows + " layoutdef(s)");
				u.close();
				
				PreparedStatement s = con.prepareStatement(
						"insert into LayoutDefinition (actclassid, typeid, xml, valid_to) " +
						"values (?, ?, ?, null)", new String[]{"ID"});
				s.setLong(1, ld.actClass.id);
				s.setInt(2, ld.type);
				s.setString(3, ld.xmlLayout);
				ld.id = Jdbc.exec(s);
				log.info("inserted new layoutdef #" + ld.id + " '" + ld.actClass.name + "'.");
				s.close();
			}
		});
	}

	@Override
	public LayoutDefinition load(Session session, String className, int typeId) {
		List<LayoutDefinition> list = listLayoutDefinitions(session, className, typeId, null, null);
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	@Override
	public List<LayoutDefinition> listLayoutDefinitions(Session session,
			final String filter, final Integer typeId, final Long entityType, final Boolean isSingleton) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<LayoutDefinition> execute(Connection con) throws SQLException {
				return listLayoutDefinitions(con, filter, typeId, entityType, isSingleton);
			}
		});
	}

	private List<LayoutDefinition> listLayoutDefinitions(Connection con,
			final String filter, final Integer typeId, final Long entityType, final Boolean isSingleton) throws SQLException {

		String sql = "select ld.id, ld.typeid, icl.summaryDef, icl.id actClassId, icl.name classname, icl.entitytype,icl.singleton, valid_To, xml  "
				+ "from LayoutDefinition ld "
				+ "inner join ActClass icl on icl.id = ld.actclassid " 
 				+ "where upper(icl.name) like ? and (valid_To >= CURRENT_TIMESTAMP or valid_to is null)";
		if (typeId != null) {
			sql += " and typeid = ?";
		}
		if (entityType != null) {
			sql += " AND (icl.entityType = ? OR icl.entityType is null)";
		}
		if (isSingleton != null) {
			sql += " AND icl.singleton = ?";
		}
		PreparedStatement s = con.prepareStatement(sql);
		int i = 1;
		s.setString(i++, filter != null ? filter.toUpperCase() + "%" : "%");
		if (typeId != null) {
			s.setInt(i++, typeId);
		}
		if (entityType != null) {
			s.setLong(i++, entityType);
		}
		if (isSingleton != null) {
			s.setBoolean(i++, isSingleton);
		}
		ResultSet rs = s.executeQuery();
		List<LayoutDefinition> result = fecthLayoutDefinitions(rs);
		rs.close();
		s.close();
		return result;
	}
	
	protected List<LayoutDefinition> fecthLayoutDefinitions(ResultSet rs) throws SQLException {
		List<LayoutDefinition> result = new ArrayList<LayoutDefinition>();
		while (rs.next()) {
			ActClass actClass = new ActClass(rs.getLong("actClassId"), rs.getString("classname"), rs.getString("summaryDef"), Jdbc.getLong(rs, "entityType"), rs.getBoolean("singleton"));
			LayoutDefinition ld = new LayoutDefinition(rs.getLong("id"), actClass, rs.getInt("typeid"), rs.getString("xml"));
			result.add(ld);
		}
		return result;
	}

	@Override
	public void delete(Session session, final String className) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("update LayoutDefinition set valid_to = CURRENT_DATE where ActClassId = (select id from actclass where Name = ?)");
				s.setString(1, className);
				int rows = s.executeUpdate();
				s.close();
				
				if (rows > 0) {
					log.info("closed layout '" + className + "'");
				}
				return null;
			}
		});
	}

	@Override
	public byte[] exportZip(Session session) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@SuppressWarnings("unchecked")
			@Override
			public byte[] execute(Connection con) throws SQLException {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ZipOutputStream zout = new ZipOutputStream(out);
					
					ZipEntry zipEntry = new ZipEntry("form/");
					zout.putNextEntry(zipEntry);
					exportType("form/", LayoutDefinition.FORM_LAYOUT, con, zout);
					
					zipEntry = new ZipEntry("print/");
					zout.putNextEntry(zipEntry);
					exportType("print/", LayoutDefinition.PRINT_LAYOUT, con, zout);
					
					zout.close();
					return out.toByteArray();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			private void exportType(final String path, final int type, Connection con,
					ZipOutputStream zout) throws SQLException, IOException,
					UnsupportedEncodingException {
				
				List<LayoutDefinition> definitions = listLayoutDefinitions(con, null, type, null, null);
				
				for (LayoutDefinition ld:definitions) {
					String name = ld.actClass.name;
					String xml = addClassInfo(ld.xmlLayout, ld.actClass);
					String entryName = path + name + ".xml";
					log.info("add: " + entryName);
					ZipEntry zipEntry = new ZipEntry(entryName);
					zout.putNextEntry(zipEntry);
					zout.write(xml.getBytes("UTF-8"));
				}
				
//				PreparedStatement s = con.prepareStatement(
//						"select name, xml from LayoutDefinition ld inner join ActClass ac on ac.id = ActClassId " +
//						"where (valid_to is null or valid_to > CURRENT_TIMESTAMP) and TypeId = ?");
//				s.setInt(1, type);
//				ResultSet rs = s.executeQuery();
//				while (rs.next()) {
//					String name = rs.getString("name");
//					String xml = rs.getString("xml");
//					String entryName = path + name + ".xml";
//					log.info("add: " + entryName);
//					ZipEntry zipEntry = new ZipEntry(entryName);
//					zout.putNextEntry(zipEntry);
//					zout.write(xml.getBytes("UTF-8"));
//				}
//				rs.close();
//				s.close();
			}

			private String addClassInfo(String xml, ActClass actClass) {
				DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db;
				try {
					db = factory.newDocumentBuilder();
					Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
					Element document = doc.getDocumentElement();
//					document.setAttribute("classid", String.valueOf(actClass.id));
					document.setAttribute("classname", actClass.name);
					if (actClass.entityType != null) {
						document.setAttribute("entitytype", String.valueOf(actClass.entityType));
					}
					document.setAttribute("singleton", String.valueOf(actClass.isSingleton));
					
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
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
				return xml;
			}
		});
	}

	@Override
	public void importZip(final Session session, final InputStream in) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@SuppressWarnings("unchecked")
			@Override
			public Void execute(Connection con) throws SQLException {
				try {
					ZipInputStream zin = new ZipInputStream(in);
					ZipEntry zipEntry = zin.getNextEntry();
					while (zipEntry != null) {
						String name = zipEntry.getName();
						ByteArrayOutputStream bOut = new ByteArrayOutputStream();
						byte[] buffer = new byte[1024];
						int read = zin.read(buffer);
						while (read != -1) {
							bOut.write(buffer, 0, read);
							read = zin.read(buffer);
						}
						String xml = new String(bOut.toByteArray(), "UTF-8");
//						log.info(name + ": " + xml);
						if (name.endsWith(".xml")) {
							int type = -1;
							if (name.startsWith("form/")) {
								type = LayoutDefinition.FORM_LAYOUT;
								name = name.substring(5);
							} else if (name.startsWith("print/")){
								type = LayoutDefinition.PRINT_LAYOUT;
								name = name.substring(6);
							} 
							
							if (type != -1) {
								ActClass actClass = getActClass(xml, name.substring(0, name.length() - 4));
								LayoutDefinition ld = new LayoutDefinition(actClass, type, xml);
								save(session, ld);
							} else {
								log.warning(name + " cannot be imported!");
							}
						}
						zipEntry = zin.getNextEntry();
					}
					zin.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return null;
			}

			private ActClass getActClass(String xml, String fileName) {
				ActClass actClass = null;
				DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db;
				try {
					db = factory.newDocumentBuilder();
					Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
					Element document = doc.getDocumentElement();
					String id = null; //document.hasAttribute("classid") ? document.getAttribute("classid") : null;
					String name = document.hasAttribute("classname") ? document.getAttribute("classname") : fileName;
					String summary = document.hasAttribute("summary") ? document.getAttribute("summary") : null;
					String entityType = null;
					if (document.hasAttribute("entitytype")) {
						entityType = document.getAttribute("entitytype");
					}
					String isSingleton = document.getAttribute("singleton");
					actClass = new ActClass(id != null ? Long.valueOf(id) : null, name, summary, entityType != null ? Long.valueOf(entityType): null, Boolean.valueOf(isSingleton));
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return actClass;
			}

			private String removeExtension(String name) {
				int i = name.toLowerCase().lastIndexOf(".xml");
				return i != -1 ? name.substring(0, i) : name;
			}
		});
	}

}

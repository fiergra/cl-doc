package com.ceres.cldoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.util.Jdbc;

public class LayoutDefinitionServiceImpl implements ILayoutDefinitionService {

	private static Logger log = Logger.getLogger("LayoutDefinitionService");

	@Override
	public void save(Session session, final LayoutDefinition ld) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				insert(con, ld, true);
				return null;
			}

			private void insert(Connection con, final LayoutDefinition ld, boolean register)
					throws SQLException {
				try {
					PreparedStatement u = con.prepareStatement(
							"update LayoutDefinition set valid_to = CURRENT_TIMESTAMP " +
							"where actclassid = (select ID from ActClass where name = ?) and typeid = ? and valid_to is null");
					u.setString(1, ld.name);
					u.setInt(2, ld.type);
					int rows = u.executeUpdate();
					log.info("closed " + rows + " layoutdef(s)");
					u.close();
					
					PreparedStatement s = con.prepareStatement(
							"insert into LayoutDefinition (actclassid, typeid, xml, valid_to) " +
							"values ((select ID from ActClass where name = ?), ?, ?, null)", new String[]{"ID"});
					s.setString(1, ld.name);
					s.setInt(2, ld.type);
					s.setString(3, ld.xmlLayout);
					ld.id = Jdbc.exec(s);
					log.info("inserted new layoutdef #" + ld.id);
					s.close();
				} catch (SQLException x) {
					if (register) {
						Locator.getActService().registerActClass(con, ld.name);
						insert(con, ld, false);
					} else {
						throw x;
					}
				}
			}
		});
	}

	@Override
	public LayoutDefinition load(Session session, String className, int typeId) {
		List<LayoutDefinition> list = listLayoutDefinitions(session, className, typeId);
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	@Override
	public List<LayoutDefinition> listLayoutDefinitions(Session session,
			final String filter, final Integer typeId) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<LayoutDefinition> execute(Connection con) throws SQLException {
				String sql = "select ld.id, ld.typeid, icl.name classname, valid_To, xml  from LayoutDefinition ld " +
						"inner join ActClass icl on icl.id = ld.actclassid " +
						"where upper(icl.name) like ? and (valid_To >= CURRENT_TIMESTAMP or valid_to is null)";
				if (typeId != null) {
					 sql += " and typeid = ?";
				}
				PreparedStatement s = con.prepareStatement(sql);
				s.setString(1, filter != null ? filter.toUpperCase() + "%" : "%");
				if (typeId != null) {
					s.setInt(2, typeId);
				}
				ResultSet rs = s.executeQuery();
				List<LayoutDefinition> result = fecthLayoutDefinitions(rs);
				rs.close();
				s.close();
				return result;
			}
		});
	}

	protected List<LayoutDefinition> fecthLayoutDefinitions(ResultSet rs) throws SQLException {
		List<LayoutDefinition> result = new ArrayList<LayoutDefinition>();
		while (rs.next()) {
			LayoutDefinition ld = new LayoutDefinition(rs.getLong("id"), rs.getInt("typeid"), rs.getString("classname"), rs.getString("xml"));
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
				PreparedStatement s = con.prepareStatement(
						"select name, xml from LayoutDefinition ld inner join ActClass ac on ac.id = ActClassId " +
						"where (valid_to is null or valid_to > CURRENT_TIMESTAMP) and TypeId = ?");
				s.setInt(1, type);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					String name = rs.getString("name");
					String xml = rs.getString("xml");
					String entryName = path + name + ".xml";
					log.info("add: " + entryName);
					ZipEntry zipEntry = new ZipEntry(entryName);
					zout.putNextEntry(zipEntry);
					zout.write(xml.getBytes("UTF-8"));
				}
				rs.close();
				s.close();
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
						log.info(name + ": " + xml);
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
								LayoutDefinition ld = new LayoutDefinition(null, 3, removeExtension(name), xml);
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

			private String removeExtension(String name) {
				int i = name.toLowerCase().lastIndexOf(".xml");
				return i != -1 ? name.substring(0, i) : name;
			}
		});
	}

}

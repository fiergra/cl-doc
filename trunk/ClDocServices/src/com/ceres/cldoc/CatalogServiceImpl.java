package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.util.Jdbc;

public class CatalogServiceImpl implements ICatalogService {

	private static Logger log = Logger.getLogger("CatalogService");

	@Override
	public void save(Session session, final Catalog catalog) {
		Catalog c = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Catalog execute(Connection con) throws SQLException {
				if (catalog.id != null) {
					PreparedStatement s = con.prepareStatement(
							"update Catalog set parent = ?, text = ?, shorttext = ?, date = ? where id = ?");
					
					int i = bindVariables(s, catalog);
					s.setLong(i, catalog.id);
					int rows = s.executeUpdate();
					s.close();
				} else {
					PreparedStatement s = con.prepareStatement("insert into Catalog (parent, text, shorttext, date, code) values (?,?,?,?,?)", new String[]{"ID"});
					bindVariables(s, catalog);
					catalog.id = Jdbc.exec(s);
					s.close();
				}
				return catalog;
			}

			private int bindVariables(PreparedStatement u,
					final Catalog catalog) throws SQLException {
				int i = 1;
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
				u.setString(i++, catalog.code);
				return i;
			}
		});
	}

	@Override
	public Catalog load(Session session, final long id) {
		Catalog result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Catalog execute(Connection con) throws SQLException {
				Catalog c = null;
				String sql = "select * from Catalog where id = ?";
				PreparedStatement s = con.prepareStatement(sql);
				s.setLong(1, id);
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					c = fetchCatalog(rs);
				}
				rs.close();
				s.close();
				return c;
			}

		});
		return result;
	}

	private Catalog fetchCatalog(ResultSet rs) throws SQLException {
		Catalog c;
		c = new Catalog(rs.getLong("id"));
		c.code = rs.getString("code");
		c.shortText = rs.getString("shorttext");
		c.text = rs.getString("text");
		c.date = rs.getDate("date");
		Long parentId = rs.getLong("parent");
		c.parent = rs.wasNull() ? null : new Catalog(parentId);
		return c;
	}

	@Override
	public Collection<Catalog> loadList(final Session session, final Catalog parent) {
		Collection<Catalog> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Collection<Catalog> execute(Connection con) throws SQLException {
				Collection<Catalog> result = new ArrayList<Catalog>();
				String sql = "select * from Catalog where parent ";
				
				if (parent == null) {
					sql += "is null";
				} else {
					sql += "= ?";
				}
				sql += " order by logical_order";
				PreparedStatement s = con.prepareStatement(sql);
				if (parent != null) {
					s.setLong(1, parent.id);
				} 
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					Catalog c = fetchCatalog(rs);
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
	public void delete(Session session, final Catalog catalog) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("delete from catalog where code = ?");
				s.setString(1, catalog.code);
				s.executeUpdate();
				s.close();
				return null;
			}
		});
	}

	private Catalog load(Session session, Connection con, String code) throws SQLException {
		Catalog c = null;
		StringTokenizer st = new StringTokenizer(code, ".");
		
		if (st.countTokens() > 1) {
			Collection<Catalog> children = loadList(session, st.nextToken());
			while (st.hasMoreTokens()) {
				c = getChild(children, st.nextToken());
				children = c.children;
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

	private Catalog doLoad(Connection con, Long parentId, String code) throws SQLException {
		String sql = "select * from Catalog where code = ? and ";
		if (parentId == null) {
			sql += "parent is null"; 
		} else {
			sql += "parent = ?"; 
		}
		
		PreparedStatement s = con.prepareStatement(sql);
		s.setString(1, code);
		if (parentId != null) {
			s.setLong(2, parentId);
		} 
		Catalog catalog = null;
		
		ResultSet rs = s.executeQuery();
		if (rs.next()) {
			catalog = fetchCatalog(rs);
		}
		rs.close();
		s.close();
		
		return catalog;
	}
	
	@Override
	public Collection<Catalog> loadList(final Session session, final String parentCode) {
		Collection<Catalog> list = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Collection<Catalog> execute(Connection con) throws SQLException {
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
}

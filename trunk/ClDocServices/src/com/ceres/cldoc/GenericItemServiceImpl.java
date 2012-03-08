package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.GenericItemField;
import com.ceres.cldoc.model.IGenericItemField;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.util.Jdbc;

public class GenericItemServiceImpl implements IGenericItemService {

	private static Logger log = Logger.getLogger("GenericItemService");

	@Override
	public void save(final Session session, final GenericItem item) {
		GenericItem i = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public GenericItem execute(Connection con) throws SQLException {
				if (item.id == null) {
					insert(con, item, true);
				} else {
					update(con, item);
				}
				
				saveFields(con, item);
				saveParticipations(session, item);
				
				return item;
			}
		});
	}

	protected void saveParticipations(Session session, GenericItem item) {
		if (item.participations != null) {
			Iterator<Participation> iter = item.participations.iterator();
			saveParticipation(session, iter.next());
		}
	}

	private void saveParticipation(Session session,	Participation participation) {
		IParticipationService participationService = Locator.getParticipationService();
		participationService.save(session, participation);
	}

	protected void saveFields(Connection con, GenericItem item) throws SQLException {
		if (item.fields != null) {
			Iterator<Entry<String, IGenericItemField>> fieldsIter = item.fields.entrySet().iterator();
			while (fieldsIter.hasNext()) {
				saveField(con, item, fieldsIter.next());
			}
		}
	}

	private void saveField(Connection con, GenericItem item, Entry<String, IGenericItemField> entry) throws SQLException {
		if (entry.getValue() != null && entry.getValue().getId() == null) {
			insertField(con, item, entry, true);
		} else {
			updateField(con, item, entry);
		}
	}

	private void updateField(Connection con, GenericItem item, Entry<String, IGenericItemField> entry) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"update ItemField set catalogValue = ?, intvalue = ?, stringvalue = ?, datevalue = ?, realvalue = ?, blobvalue = ? where id = ?");
		String fieldName = entry.getKey();
		IGenericItemField field = entry.getValue();
		int i = bindVariables(s, 1, item, fieldName, field);
		s.setLong(i, field.getId());
		int rows = s.executeUpdate();
		s.close();
	}

	private void insertField(Connection con, GenericItem item, Entry<String, IGenericItemField> entry, boolean register) throws SQLException {
		try {
			PreparedStatement s = con.prepareStatement(
					"insert into ItemField (itemid, classfieldid, CatalogValue, intvalue, stringvalue, datevalue, realvalue, blobvalue) values (?, (select id from ItemClassField where name = ?), ?, ?, ?, ?, ?, ?)", new String[]{"ID"});
			String fieldName = entry.getKey();
			IGenericItemField field = entry.getValue();
			s.setLong(1, item.id);
			s.setString(2, fieldName);
			bindVariables(s, 3, item, fieldName, field);
			field.setId(Jdbc.exec(s));
			s.close();
		} catch (SQLException x) {
			if (register && registerClassField(con, item, entry.getKey(), entry.getValue().getType())) {
				insertField(con, item, entry, false);
			} else {
				throw x;
			}
		}
	}

	private boolean registerClassField(Connection con, GenericItem item, String fieldName, int type) throws SQLException {
		PreparedStatement s = con.prepareStatement("insert into ItemClassField (itemclassid, name, type) values ((select id from ItemClass where name = ?),?,?)");
		s.setString(1, item.className);
		s.setString(2, fieldName);
		s.setInt(3, type);
		
		int rows = s.executeUpdate();
		s.close();
		log.info("registered new field '" + fieldName + "(" + type + ")' in item class '" + item.className + "'");
		return rows == 1;
	}


	private int bindVariables(PreparedStatement s, int i, GenericItem item,
			String fieldName, IGenericItemField field)
			throws SQLException {
//itemid, classfieldid, catalog, intvalue, stringvalue, datevalue, realvalue, blobvalue
		if (field.getType() == IGenericItemField.FT_CATALOG) {
			s.setLong(i++, field.getCatalogValue().id);
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		if (field.getType() == IGenericItemField.FT_INTEGER || field.getType() == IGenericItemField.FT_BOOLEAN) {
			s.setLong(i++, field.getLongValue());
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		if (field.getType() == IGenericItemField.FT_STRING) {
			s.setString(i++, field.getStringValue());
		} else {
			s.setNull(i++, Types.VARCHAR);
		}
		if (field.getType() == IGenericItemField.FT_DATE) {
			s.setTimestamp(i++, new java.sql.Timestamp(field.getDateValue().getTime()));
		} else {
			s.setNull(i++, Types.TIMESTAMP);
		}
		// not yet supported...
		s.setNull(i++, Types.FLOAT);
		if (field.getType() == IGenericItemField.FT_BLOB) {
			s.setBytes(i++, field.getBlobValue());
		} else {
			s.setNull(i++, Types.BLOB);
		}
		
		return i;
	}

	protected GenericItem update(Connection con, GenericItem item) throws SQLException {
		PreparedStatement s = con.prepareStatement("update Item set Date = ? where id = ?");
		s.setTimestamp(1, new java.sql.Timestamp(item.date != null ? item.date.getTime() : new Date().getTime()));
		s.setLong(2, item.id);
		s.executeUpdate();
		s.close();
		return item;
	}

	protected GenericItem insert(Connection con, GenericItem item, boolean register) throws SQLException {
		try {
			PreparedStatement s = con.prepareStatement("insert into Item (ItemClassId,Date) values ((select id from ItemClass where name = ?), ?)",
					new String[]{"ID"});
			s.setString(1, item.className);
			if (item.date != null) {
				s.setTimestamp(2, new java.sql.Timestamp(item.date.getTime()));
			} else {
				s.setNull(2, Types.TIMESTAMP);
			}
			item.id = Jdbc.exec(s);
			s.close();
		} catch (SQLException x) {
			if (register && registerItemClass(con, item.className)) {
				insert(con, item, false);
			} else {
				throw x;
			}
		}
		return item;
	}

	@Override
	public boolean registerItemClass(Connection con, String className) throws SQLException {
		PreparedStatement s = con.prepareStatement("insert into ItemClass (name) values (?)");
		s.setString(1, className);
		int rows = s.executeUpdate();
		s.close();
		log.info("registered new item class '" + className + "'");
		return rows == 1;
	}

	@Override
	public List<GenericItem> load(final Session session, final AbstractEntity entity) {
		List<GenericItem> items = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<GenericItem> execute(Connection con) throws SQLException {
				return executeSelect(session, con, null, entity);
			}
		});
		
		return items;
	}

	private List<GenericItem> executeSelect(Session session, Connection con, Long id, AbstractEntity entity) throws SQLException {
		String sql = "select " +
				"i.id itemid, i.date, itemclass.name classname, itemclassfield.name fieldname, itemclassfield.type, field.* " +
				"from Item i " +
				"left outer join ItemField field on i.id = field.itemid " +
				"left outer join ItemClassField itemclassfield on field.classfieldid = itemclassfield.id " +
				"inner join ItemClass itemclass on i.itemclassid = itemclass.id where 1=1 ";
				
		if (id != null) {
			sql += "and i.id = ? ";
		}
		if (entity != null) {
			sql += "and i.id in (select itemid from Participation where entityid = ?) ";
		}
		
		int i = 1;
		PreparedStatement s = con.prepareStatement(sql);
		if (id != null) {
			s.setLong(i++, id);
		}
		if (entity != null) {
			s.setLong(i++, entity.id);
		}
		ResultSet rs = s.executeQuery();
		List<GenericItem>items = fetchItems(session, rs);
		rs.close();
		s.close();
		
		return items;
	}

	
	@Override
	public GenericItem load(final Session session, final long id) {
		GenericItem item = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public GenericItem execute(Connection con) throws SQLException {
				Collection<GenericItem>items = executeSelect(session, con, id, null);
				return items.isEmpty() ? null : items.iterator().next();
			}
		});
		
		IParticipationService participationService = Locator.getParticipationService();
		item.participations = participationService.load(session, item);
		return item;
	}

	private List<GenericItem> fetchItems(Session session, ResultSet rs) throws SQLException {
		List<GenericItem> items = new ArrayList<GenericItem>();
		GenericItem item = null;
		ICatalogService catalogService = Locator.getCatalogService();
		while (rs.next()) {
			long itemId = rs.getLong("itemid");
			if (item == null || !item.id.equals(itemId)) {
				item = new GenericItem(rs.getString("classname"));
				item.id = itemId;
				Timestamp timeStamp = rs.getTimestamp("date");
				item.date = rs.wasNull() ? null : new Date(timeStamp.getTime());
				items.add(item);
			}
			long fieldId = rs.getLong("id");
			String fieldName = rs.getString("fieldname");
			int type = rs.getInt("type");
			
			if (!rs.wasNull()) {
				GenericItemField field = new GenericItemField(fieldId, fieldName, type);
				Timestamp timeStamp;
				switch (type) {
				case IGenericItemField.FT_BOOLEAN:
					Long lvalue = rs.getLong("intvalue");
					field.setValue(lvalue.equals(1));
				case IGenericItemField.FT_INTEGER:
					field.setValue(rs.getLong("intvalue"));
					break;
				case IGenericItemField.FT_STRING:
					field.setValue(rs.getString("stringvalue"));
					break;
				case IGenericItemField.FT_DATE:
					timeStamp = rs.getTimestamp("datevalue");
					field.setValue(rs.wasNull() ? (Date)null : new Date(timeStamp.getTime()));
					break;
				case IGenericItemField.FT_CATALOG:
					Long id = rs.getLong("catalogValue");
					field.setValue(rs.wasNull() ? (Catalog)null : catalogService.load(session, id));
					break;
				case IGenericItemField.FT_BLOB:
					byte[] bytes = rs.getBytes("BlobValue");
					field.setValue(bytes);
					break;
				}
				item.addField(field);
			}			
		}
		return items;
	}

	@Override
	public void delete(Session session, final GenericItem item) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("delete from Participation where itemid = ?");
				s.setLong(1, item.id);
				int rows = s.executeUpdate();
				log.info("deleted " + rows + " participation(s).");
				s.close();
				
				s = con.prepareStatement("delete from ItemField where itemid = ?");
				s.setLong(1, item.id);
				rows = s.executeUpdate();
				log.info("deleted " + rows + " field(s).");
				s.close();
				
				s = con.prepareStatement("delete from Item where id = ?");
				s.setLong(1, item.id);
				rows = s.executeUpdate();
				log.info("deleted item #" + item.id);
				s.close();
				
				return null;
			}
		});
	}

}

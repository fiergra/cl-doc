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
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActField;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.util.Jdbc;

public class ActServiceImpl implements IActService {

	private static Logger log = Logger.getLogger("GenericActService");

	@Override
	public void save(final Session session, final Act act) {
		Act i = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Act execute(Connection con) throws SQLException {
				if (act.id == null) {
					insert(con, act, true);
					Locator.getLogService().log(session, ILogService.INSERT, act, act.snapshot());
				} else {
					update(con, act);
					Locator.getLogService().log(session, ILogService.UPDATE, act, act.snapshot());
				}
				
				saveFields(con, act);
				saveParticipations(session, act);
				
				
				return act;
			}
		});
	}

	protected void saveParticipations(Session session, Act act) {
		if (act.participations != null) {
			Iterator<Participation> iter = act.participations.iterator();
			saveParticipation(session, iter.next());
		}
	}

	private void saveParticipation(Session session,	Participation participation) {
		IParticipationService participationService = Locator.getParticipationService();
		participationService.save(session, participation);
	}

	protected void saveFields(Connection con, Act act) throws SQLException {
		if (act.fields != null) {
			Iterator<Entry<String, IActField>> fieldsIter = act.fields.entrySet().iterator();
			while (fieldsIter.hasNext()) {
				saveField(con, act, fieldsIter.next());
			}
		}
	}

	private void saveField(Connection con, Act act, Entry<String, IActField> entry) throws SQLException {
		if (entry.getValue() != null && entry.getValue().getId() == null) {
			insertField(con, act, entry, true);
		} else {
			updateField(con, act, entry);
		}
	}

	private void updateField(Connection con, Act act, Entry<String, IActField> entry) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"update ActField set catalogValue = ?, intvalue = ?, stringvalue = ?, datevalue = ?, realvalue = ?, blobvalue = ? where id = ?");
		String fieldName = entry.getKey();
		IActField field = entry.getValue();
		int i = bindVariables(s, 1, act, fieldName, field);
		s.setLong(i, field.getId());
		int rows = s.executeUpdate();
		s.close();
	}

	private void insertField(Connection con, Act act, Entry<String, IActField> entry, boolean register) throws SQLException {
		try {
			PreparedStatement s = con.prepareStatement(
					"insert into ActField (actid, classfieldid, CatalogValue, intvalue, stringvalue, datevalue, realvalue, blobvalue) values (?, (select id from ActClassField where name = ?), ?, ?, ?, ?, ?, ?)", new String[]{"ID"});
			String fieldName = entry.getKey();
			IActField field = entry.getValue();
			s.setLong(1, act.id);
			s.setString(2, fieldName);
			bindVariables(s, 3, act, fieldName, field);
			field.setId(Jdbc.exec(s));
			s.close();
		} catch (SQLException x) {
			if (register && registerClassField(con, act, entry.getKey(), entry.getValue().getType())) {
				insertField(con, act, entry, false);
			} else {
				throw x;
			}
		}
	}

	private boolean registerClassField(Connection con, Act act, String fieldName, int type) throws SQLException {
		PreparedStatement s = con.prepareStatement("insert into ActClassField (actclassid, name, type) values ((select id from ActClass where name = ?),?,?)");
		s.setString(1, act.className);
		s.setString(2, fieldName);
		s.setInt(3, type);
		
		int rows = s.executeUpdate();
		s.close();
		log.info("registered new field '" + fieldName + "(" + type + ")' in act class '" + act.className + "'");
		return rows == 1;
	}


	private int bindVariables(PreparedStatement s, int i, Act act,
			String fieldName, IActField field)
			throws SQLException {
//actid, classfieldid, catalog, intvalue, stringvalue, datevalue, realvalue, blobvalue
		if (field.getType() == IActField.FT_CATALOG) {
			s.setLong(i++, field.getCatalogValue().id);
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		if (field.getType() == IActField.FT_INTEGER || field.getType() == IActField.FT_BOOLEAN) {
			s.setLong(i++, field.getType() == IActField.FT_INTEGER ? field.getLongValue() : (field.getBooleanValue() ? 1 : 0));
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		if (field.getType() == IActField.FT_STRING) {
			s.setString(i++, field.getStringValue());
		} else {
			s.setNull(i++, Types.VARCHAR);
		}
		if (field.getType() == IActField.FT_DATE) {
			s.setTimestamp(i++, new java.sql.Timestamp(field.getDateValue().getTime()));
		} else {
			s.setNull(i++, Types.TIMESTAMP);
		}
		// not yet supported...
		s.setNull(i++, Types.FLOAT);
		if (field.getType() == IActField.FT_BLOB) {
			s.setBytes(i++, field.getBlobValue());
		} else {
			s.setNull(i++, Types.BLOB);
		}
		
		return i;
	}

	protected Act update(Connection con, Act act) throws SQLException {
		PreparedStatement s = con.prepareStatement("update Act set Date = ? where id = ?");
		s.setTimestamp(1, new java.sql.Timestamp(act.date != null ? act.date.getTime() : new Date().getTime()));
		s.setLong(2, act.id);
		s.executeUpdate();
		s.close();
		return act;
	}

	protected Act insert(Connection con, Act act, boolean register) throws SQLException {
		try {
			PreparedStatement s = con.prepareStatement("insert into Act (ActClassId,Date) values ((select id from ActClass where name = ?), ?)",
					new String[]{"ID"});
			s.setString(1, act.className);
			if (act.date != null) {
				s.setTimestamp(2, new java.sql.Timestamp(act.date.getTime()));
			} else {
				s.setNull(2, Types.TIMESTAMP);
			}
			act.id = Jdbc.exec(s);
			s.close();
		} catch (SQLException x) {
			if (register && registerActClass(con, act.className)) {
				insert(con, act, false);
			} else {
				throw x;
			}
		}
		return act;
	}

	@Override
	public boolean registerActClass(Connection con, String className) throws SQLException {
		PreparedStatement s = con.prepareStatement("insert into ActClass (name) values (?)");
		s.setString(1, className);
		int rows = s.executeUpdate();
		s.close();
		log.info("registered new act class '" + className + "'");
		return rows == 1;
	}

	@Override
	public List<Act> load(final Session session, final AbstractEntity entity) {
		List<Act> acts = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Act> execute(Connection con) throws SQLException {
				return executeSelect(session, con, null, entity);
			}
		});
		
		return acts;
	}

	private List<Act> executeSelect(Session session, Connection con, Long id, AbstractEntity entity) throws SQLException {
		String sql = "select " +
				"i.id actid, i.date, actclass.name classname, actclassfield.name fieldname, actclassfield.type, field.* " +
				"from Act i " +
				"left outer join ActField field on i.id = field.actid " +
				"left outer join ActClassField actclassfield on field.classfieldid = actclassfield.id " +
				"inner join ActClass actclass on i.actclassid = actclass.id where 1=1 ";
				
		if (id != null) {
			sql += "and i.id = ? ";
		}
		if (entity != null) {
			sql += "and i.id in (select actid from Participation where entityid = ?) ";
		}
		
		sql += " order by i.date desc";
		int i = 1;
		PreparedStatement s = con.prepareStatement(sql);
		if (id != null) {
			s.setLong(i++, id);
		}
		if (entity != null) {
			s.setLong(i++, entity.id);
		}
		ResultSet rs = s.executeQuery();
		List<Act>acts = fetchActs(session, rs);
		rs.close();
		s.close();
		
		return acts;
	}

	
	@Override
	public Act load(final Session session, final long id) {
		Act act = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Act execute(Connection con) throws SQLException {
				Collection<Act>acts = executeSelect(session, con, id, null);
				return acts.isEmpty() ? null : acts.iterator().next();
			}
		});
		
		IParticipationService participationService = Locator.getParticipationService();
		act.participations = participationService.load(session, act);
		return act;
	}

	private List<Act> fetchActs(Session session, ResultSet rs) throws SQLException {
		List<Act> acts = new ArrayList<Act>();
		Act act = null;
		ICatalogService catalogService = Locator.getCatalogService();
		while (rs.next()) {
			long actId = rs.getLong("actid");
			if (act == null || !act.id.equals(actId)) {
				act = new Act(rs.getString("classname"));
				act.id = actId;
				Timestamp timeStamp = rs.getTimestamp("date");
				act.date = rs.wasNull() ? null : new Date(timeStamp.getTime());
				acts.add(act);
			}
			long fieldId = rs.getLong("id");
			String fieldName = rs.getString("fieldname");
			int type = rs.getInt("type");
			
			if (!rs.wasNull()) {
				ActField field = new ActField(fieldId, fieldName, type);
				Timestamp timeStamp;
				switch (type) {
				case IActField.FT_BOOLEAN:
					Long lvalue = rs.getLong("intvalue");
					field.setValue(lvalue.equals(1));
				case IActField.FT_INTEGER:
					field.setValue(rs.getLong("intvalue"));
					break;
				case IActField.FT_STRING:
					field.setValue(rs.getString("stringvalue"));
					break;
				case IActField.FT_DATE:
					timeStamp = rs.getTimestamp("datevalue");
					field.setValue(rs.wasNull() ? (Date)null : new Date(timeStamp.getTime()));
					break;
				case IActField.FT_CATALOG:
					Long id = rs.getLong("catalogValue");
					field.setValue(rs.wasNull() ? (Catalog)null : catalogService.load(session, id));
					break;
				case IActField.FT_BLOB:
					byte[] bytes = rs.getBytes("BlobValue");
					field.setValue(bytes);
					break;
				}
				act.addField(field);
			}			
		}
		return acts;
	}

	@Override
	public void delete(Session session, final Act act) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("delete from Participation where actid = ?");
				s.setLong(1, act.id);
				int rows = s.executeUpdate();
				log.info("deleted " + rows + " participation(s).");
				s.close();
				
				s = con.prepareStatement("delete from ActField where actid = ?");
				s.setLong(1, act.id);
				rows = s.executeUpdate();
				log.info("deleted " + rows + " field(s).");
				s.close();
				
				s = con.prepareStatement("delete from Act where id = ?");
				s.setLong(1, act.id);
				rows = s.executeUpdate();
				log.info("deleted act #" + act.id);
				s.close();
				
				return null;
			}
		});
	}

	@Override
	public List<String> listClassNames(Session session, final String filter) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List <String> execute(Connection con) throws SQLException {
				List <String> result = new ArrayList<String>();
				PreparedStatement s = con.prepareStatement("select * from actclass where upper(Name) like ? order by Name");
				s.setString(1, filter != null ? filter.toUpperCase() + "%" : "%");
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					result.add(rs.getString("Name"));
				}
				return result;
			}
		});
	}

}

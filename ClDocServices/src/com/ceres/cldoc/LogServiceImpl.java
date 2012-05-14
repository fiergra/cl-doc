package com.ceres.cldoc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.IActField;
import com.ceres.cldoc.model.LogEntry;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.util.Jdbc;

public class LogServiceImpl implements ILogService {

	private static Logger log = Logger.getLogger("LogService");

	@Override
	public void log(final Session session, final int type, final Act act, final String logEntry) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("insert into LogEntry (UserId, ActId, EntityId, type, logEntry) values (?, ?, ?, ?, ?)");
				int i = 1;
				s.setLong(i++, session.getUser().id);
				if (act.id != null) {
					s.setLong(i++, act.id);
				} else {
					s.setNull(i++, Types.INTEGER);
				}
				Participation participation = act.getParticipation(0);
				AbstractEntity entity = participation != null ? participation.entity : null;
				
				if (entity != null && entity.id != null) {
					s.setLong(i++, entity.id);
				} else {
					s.setNull(i++, Types.INTEGER);
				}
				s.setInt(i++, type);
				s.setString(i++, logEntry);
				s.execute();
				return null;
			}
		});
	}

	@Override
	public List<LogEntry> listRecent(final Session session) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<LogEntry> execute(Connection con) throws SQLException {
				List<LogEntry> result = new ArrayList<LogEntry>();
				PreparedStatement s = con.prepareStatement(
						"select pers.id entId, pers.*, le.id leId, le.actId, le.type, le.logDate, le.logEntry, a.date actDate, acf.name fieldName, acf.type fieldType, ac.name classname, af.id actFieldId, af.* " +
						"from LogEntry le " +
						"left outer join Act a on le.actId = a.id " +
						"inner join Participation part on part.actId = a.id AND role = 101 " +
						"inner join Person pers on part.EntityId = pers.id " +
						"left outer join ActClass ac on ac.id = a.ActClassid " +
						"left outer join ActField af on af.actId = a.id " +
						"left outer join ActClassField acf on acf.id = af.ClassFieldId " +
						"where userId = ? " +
						"order by logDate desc ");
				s.setLong(1, session.getUser().id);
				ResultSet rs = s.executeQuery();
				LogEntry le = null;
				Act act = null;
				while (rs.next()) {
					long actId = rs.getLong("actId");
					long leId = rs.getLong("leId");
					if (act == null || act.id != actId) {
						act = new Act(rs.getString("classname"));
						act.id = actId;
						act.date = rs.getTimestamp("actDate");
					}
					if (le == null || le.id != leId) {
						le = new LogEntry(leId, rs.getInt("type"), act, null, rs.getString("logEntry"), rs.getTimestamp("logDate"));
						Person person = new Person();
						person.id = rs.getLong("entId");
						if (!rs.wasNull()) {
							person.firstName = rs.getString("firstName");
							person.lastName = rs.getString("lastName");
							person.perId  = rs.getLong("per_id");
							le.entity = person;
						}
						result.add(le);
					}
					String fieldName = rs.getString("fieldName");
					if (!rs.wasNull()) {
						int type = rs.getInt("fieldType");
						IActField actField = act.set(fieldName, getValue(rs, type));
						actField.setId(rs.getLong("actFieldId"));
					}
				}
				rs.close();
				s.close();
				return result;
			}

			private Serializable getValue(ResultSet rs, int type) throws SQLException {
				Serializable result = null;
				switch (type) {
				case IActField.FT_STRING: result = rs.getString("stringValue"); break;
				case IActField.FT_INTEGER: result = rs.getLong("intValue"); break;
				case IActField.FT_DATE: result = rs.getString("dateValue"); break;
				case IActField.FT_BOOLEAN: result = rs.getInt("intValue") == 1; break;
				case IActField.FT_CATALOG: 
					long catalogId = rs.getLong("CatalogValue");
					result = rs.wasNull() ? null : Locator.getCatalogService().load(session, catalogId); 
					break;
				case IActField.FT_BLOB: 
					result = rs.getBytes("BlobValue");
					break;
				}
				return result;
			}
		});
	}

}

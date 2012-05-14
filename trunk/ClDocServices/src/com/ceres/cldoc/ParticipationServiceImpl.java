package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.util.Jdbc;

public class ParticipationServiceImpl implements IParticipationService {

	private static Logger log = Logger.getLogger("ParticipationService");

	@Override
	public void save(Session session, final Participation participation) {
		Participation p = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Participation execute(Connection con) throws SQLException {
				if (participation.id == null) {
					insertParticipation(con, participation);
				} else {
					updateParticipation(con, participation);
				}
				return null;
			}
		});
		
	}

	protected void updateParticipation(Connection con,
			Participation participation) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"update Participation set role = ?, startdate = ?, enddate = ? where id = ?");
		int i = 1;
		s.setLong(i++, participation.role.id);
		s.setTimestamp(i++, new java.sql.Timestamp(participation.start.getTime()));
		if (participation.end != null) {
			s.setTimestamp(i++, new java.sql.Timestamp(participation.end.getTime()));
		} else {
			s.setNull(i++, Types.TIMESTAMP);
		}
		s.setLong(i++, participation.id);
		s.executeUpdate();
		s.close();
	}

	protected void insertParticipation(Connection con,
			Participation participation) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"insert into Participation (actid, entityid, role, startdate, enddate) values (?, ?, ?, ?, ?) ", new String[]{"ID"});
		int i = 1;
		s.setLong(i++, participation.act.id);
		s.setLong(i++, participation.entity.id);
		s.setLong(i++, participation.role.id);
		s.setTimestamp(i++, new java.sql.Timestamp(participation.start.getTime()));

		if (participation.end != null) {
			s.setTimestamp(i++, new java.sql.Timestamp(participation.end.getTime()));
		} else {
			s.setNull(i++, Types.TIMESTAMP);
		}
		participation.id = Jdbc.exec(s);
		s.close();
	}

	@Override
	public Collection<Participation> load(final Session session, final Act act) {
		Collection<Participation> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Collection<Participation> execute(Connection con) throws SQLException {
				IEntityService entityService = Locator.getEntityService();
				Collection<Participation> result = new ArrayList<Participation>();		
				PreparedStatement s = con.prepareStatement("select * from Participation where actid = ?");
				s.setLong(1, act.id);
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					Participation p = new Participation();
					p.act = act;
					p.entity = entityService.load(session, rs.getLong("entityid"));
					p.role = getRole(session, rs.getLong("role"));
					p.start = new Date(rs.getTimestamp("startdate").getTime());
					p.end = rs.getTimestamp("enddate");
					if (rs.wasNull()) {
						p.end = null;
					}
					result.add(p);
				}
				rs.close();
				s.close();
				return result;
			}
		});
		
		
		return result;
	}

	protected Catalog getRole(Session session, long roleId) {
		return roleId == 101l ? Catalog.PATIENT : Locator.getCatalogService().load(session, roleId);
	}

}
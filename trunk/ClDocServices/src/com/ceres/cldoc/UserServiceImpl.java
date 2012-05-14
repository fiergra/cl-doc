package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.cldoc.util.Strings;

public class UserServiceImpl implements IUserService {

	private static Logger log = Logger.getLogger("UserService");

	@Override
	public Session login(final Session session, final String userName, final String password) {
		User user = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public User execute(Connection con) throws SQLException {
				IEntityService entityService = Locator.getEntityService();
				User user = null;
				String hash = Strings.hash(password);
				String sql = "select * from User u inner join Organisation o on o.ID = ORGANISATION_ID " +
						"where u.name = ? ";
				if (hash == null) {
					sql += " and hash is null";
				} else {
					sql += " and hash = ?";
				}
				PreparedStatement s = con.prepareStatement(sql);
				s.setString(1, userName);
				if (hash != null) {
					s.setString(2, hash);
				}
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					user = new User();
					user.id = rs.getLong("id");
					user.userName = userName;
					user.person = entityService.load(session, rs.getLong("person_id"));
					user.organisation = entityService.load(session, rs.getLong("organisation_id"));
					user.hash = rs.getString("hash");
				}
				rs.close();
				s.close();
				return user;
			}
		});
		
		return (user != null) ? new Session(user) : null;
	}

	@Override
	public void register(final Session session, final Person person, final Organisation organisation, final String userName, final String password) {
		User user = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public User execute(Connection con) throws SQLException {
				IEntityService entityService = Locator.getEntityService();
				User user = null;
				String hash = Strings.hash(password);
				PreparedStatement s = con.prepareStatement("select * from User where name = ? and hash = ?");
				s.setString(1, userName);
				s.setString(2, hash);
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					user = new User();
					user.id = rs.getLong("id");
					user.userName = userName;
					user.person = entityService.load(session, rs.getLong("person_id"));
					if (isDifferentPerson(person, user.person)) {
						user = null;
					}
				} else {
					entityService.save(session, person);
					PreparedStatement i = con.prepareStatement("insert into User (person_id, organisation_id, name, hash) values (?,?,?,?)", new String[]{"ID"});
					i.setLong(1, person.id);
					i.setLong(2, organisation.id);
					i.setString(3, userName);
					i.setString(4, hash);
					user = new User();
					user.id = Jdbc.exec(i);
					user.person = person;
					i.close();
				}
				rs.close();
				s.close();
				return user;
			}

			private boolean isDifferentPerson(Person person, Person person2) {
				return !person.name.equals(person2.name);
			}
		});		
	}

	@Override
	public long setPassword(Session session, final User user, final String password1, String password2) {
		long returnCode; 
		
		if (password1 != null && password2 != null) {
			if (password1.equals(password2)) {
				returnCode = Jdbc.doTransactional(session, new ITransactional() {
					
					@Override
					public Long execute(Connection con) throws SQLException {
						String hash = Strings.hash(password1);
						PreparedStatement s = con.prepareStatement("update User set hash = ? where id = ?");
						s.setString(1, hash);
						s.setLong(2, user.id);
						int rows = s.executeUpdate();
						s.close();
						
						return rows == 1 ? SUCCESS : NO_USER;
					}
				});
				
			} else {
				returnCode = NOT_EQUAL;
			}
		} else {
			returnCode = NOT_DEFINED;
		}
		return returnCode;
	}

}

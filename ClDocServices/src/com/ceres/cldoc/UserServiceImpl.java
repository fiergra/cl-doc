package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.ISession;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.ceres.cldoc.security.AccessControl;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.cldoc.util.Strings;

public class UserServiceImpl implements IUserService {

	private static Logger log = Logger.getLogger("UserService");

	@Override
	public ISession login(final ISession session, final String userName, final String password) {
		ISession s = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public ISession execute(Connection con) throws SQLException {
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
				
				return user != null ? new Session(user, con.getMetaData().getUserName(), con.getMetaData().getURL(), AccessControl.get(session, user, null)) : null;
			}
		});
		return s;
	}

	@Override
	public User register(final ISession session, final Person person, final Organisation organisation, final String userName, final String password) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
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
					i.setLong(1, person.getId());
					i.setLong(2, organisation.getId());
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
				return !person.getName().equals(person2.getName());
			}
		});		
	}

	@Override
	public long setPassword(ISession session, final User user, final String password1, String password2) {
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

	@Override
	public List<User> listUsers(final ISession session, final String filter) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<User> execute(Connection con) throws SQLException {
				List<User> result = new ArrayList<User>();
				PreparedStatement s = con.prepareStatement("select u.id userId, u.name userName, p.id entityId, firstname, lastname, o.id organisationId, o.name, o.type from User u inner join Entity o on o.id = organisation_id inner join Person p on p.id = person_id where lower(u.name) like ?");
				s.setString(1, filter.toLowerCase() + "%");
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					User u = new User();
					u.id = rs.getLong("userId");
					u.userName = rs.getString("userName");
					Person p = new Person();
					p.setId(rs.getLong("entityId"));
					p.firstName = rs.getString("firstName");
					p.lastName = rs.getString("lastName");
					u.person = p;
					Organisation o = new Organisation();
					o.setId(rs.getLong("organisationId"));
					o.setName(rs.getString("name"));
					o.setType(rs.getInt("type"));
					u.organisation = o;
					
					AccessControl.get(session, u, null);
					result.add(u);
				}
				rs.close();
				s.close();
				return result;
			}
		});
	}

	@Override
	public void addRole(ISession session, final User user, final Catalog role) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("insert into Assignment(userid, role, startdate) values (?, ?, CURRENT_DATE);");
				s.setLong(1, user.id);
				s.setLong(2, role.id);
				int rows = s.executeUpdate();
				return null;
			}
		});
	}

	@Override
	public void removeRole(ISession session, final User user, final Catalog role) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("update Assignment set enddate = CURRENT_DATE where userid = ? and role = ?");
				s.setLong(1, user.id);
				s.setLong(2, role.id);
				int rows = s.executeUpdate();
				return null;
			}
		});
	}

}

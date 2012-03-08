package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Address;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.util.Jdbc;

public class EntityServiceImpl implements IEntityService {

	private static Logger log = Logger.getLogger("EntityService");

	@Override
	public void save(Session session, final AbstractEntity entity) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				if (entity.id == null) {
					insertEntity(con, entity);
					if (entity instanceof Person) {
						insertPerson(con, (Person) entity);
					}
				} else {
					updateEntity(con, entity);
					if (entity instanceof Person) {
						updatePerson(con, (Person) entity);
					}
				}

				saveAddresses(con, entity);
				return null;
			}
		});
	}

	private void saveAddresses(Connection con, AbstractEntity entity)
			throws SQLException {
		if (entity.addresses != null) {
			for (Address a : entity.addresses) {
				saveAddress(con, a);
			}
		}
	}

	private void saveAddress(Connection con, Address a) throws SQLException {
		if (a.id == null) {
			insertAddress(con, a);
		} else {
			updateAddress(con, a);
		}
	}

	private void updateAddress(Connection con, Address a) throws SQLException {
		PreparedStatement s = con
				.prepareStatement("update Address set street = ?, number = ?, co = ?, postcode = ?, city = ? where id = ?");
		int i = 1;
		s.setString(i++, a.street);
		s.setString(i++, a.number);
		s.setString(i++, a.co);
		s.setString(i++, a.postCode);
		s.setString(i++, a.city);
		s.setLong(i++, a.id);
		s.executeUpdate();
		s.close();
	}

	private void insertAddress(Connection con, Address a) throws SQLException {
		PreparedStatement s = con
				.prepareStatement(
						"insert into Address (Entity_id, street, number, co, postcode, city) values (?,?,?,?,?,?)",
						new String[] { "ID" });
		int i = 1;
		s.setLong(i++, a.entity.id);
		s.setString(i++, a.street);
		s.setString(i++, a.number);
		s.setString(i++, a.co);
		s.setString(i++, a.postCode);
		s.setString(i++, a.city);
		a.id = Jdbc.exec(s);
		s.close();
	}

	private void updateEntity(Connection con, AbstractEntity entity)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("update Entity set name = ?, type = ? where id = ?");
		s.setString(1, entity.name);
		s.setInt(2, entity.type);
		s.setLong(3, entity.id);
		s.executeUpdate();
		s.close();
	}

	private void insertEntity(Connection con, AbstractEntity entity)
			throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"insert into Entity (name,type) values (?,?)",
				new String[] { "ID" });
		s.setString(1, entity.name);
		s.setInt(2, entity.type);
		entity.id = Jdbc.exec(s);
		s.close();
	}

	private void insertPerson(Connection con, Person person)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("insert into Person(id,per_id,firstname,lastname,sndx_firstname,sndx_lastname) values (?,?,?,?,soundex(?),soundex(?))");
		int i = 1;
		s.setLong(i++, person.id);
		s.setLong(i++, person.perId);
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		s.executeUpdate();
		s.close();
	}

	private void updatePerson(Connection con, Person person)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("update Person set firstname = ?,lastname = ?, sndx_firstname = soundex(?), sndx_lastname = soundex(?) where id = ?");
		int i = 1;
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		s.setLong(i++, person.id);
		s.close();
	}

	@SuppressWarnings("unchecked")
	private <T extends AbstractEntity> T load(Connection con, long id) throws SQLException {
		String sql = "select e.id entityId, pers.firstname, pers.lastname, pers.dateofbirth, e.type, adr.id addressId, street, number, city, postcode, co from Entity e "
				+ "left outer join Person pers on pers.id = e.id "
				+ "left outer join Address adr on adr.entity_id = e.id " 
				+ "where e.id = ?";
//				+ "order by e.id";
		PreparedStatement s = con.prepareStatement(sql);
		s.setLong(1, id);
		ResultSet rs = s.executeQuery();
		T e = null;
		
		while (rs.next()) {
			long entityId = rs.getLong("entityId");
			if (e == null || !e.id.equals(entityId)) {
				switch (rs.getInt("type")) {
				case 1: 
					Person p = fetchPerson(rs, entityId);
					e = (T) p;
				break;
				}
			}
			Long addressId = rs.getLong("addressId");
			if (!rs.wasNull()) {
				Address a = new Address();
				a.id = addressId;
				a.street = rs.getString("street");
				a.number = rs.getString("number");
				a.postCode = rs.getString("postcode");
				a.city = rs.getString("city");
				a.co = rs.getString("co");
				e.addAddress(a);
			}
		}
		return e;
	}

	private Person fetchPerson(ResultSet rs, long entityId) throws SQLException {
		Person p = new Person();
		p.firstName = rs.getString("firstname");
		p.lastName = rs.getString("lastname");
		p.id = entityId;
		p.dateOfBirth = rs.getDate("dateofbirth");
		return p;
	}

	@Override
	public <T extends AbstractEntity> T load(Session session, final long id) {
		T e = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public T execute(Connection con) throws SQLException {
				return load(con, id);
			}
		});
		return e;
	}

	@Override
	public List<Person> search(Session session, final String filter) {
		List<Person> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Person> execute(Connection con) throws SQLException {
				return selectPersons(con, filter, null);
			}
		});
		
		return result;
	}
	
	
	private List<Person> selectPersons(Connection con, String filter, String role) throws SQLException {
		Collection<String> names = new ArrayList<String>();
		String sql = "select * from Person p ";
		String where = " where 1=1 ";
		
		if (role != null) {
			sql +=  " inner join Assignment a on a.EntityId = p.Id" +
					" inner join Catalog c on a.type = c.Id";
			where += " AND c.code = ?";
		}
		if (filter != null) {
			StringTokenizer st = new StringTokenizer(filter);
			Collection<Long> ids = new ArrayList<Long>();
			
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.matches("[0-9]*")) {
					ids.add(Long.valueOf(token));
				} else {
					names.add(token);
				}
			}
			
			for (String name: names) {
				where += " AND (sndx_firstname = soundex(?) OR sndx_lastname = soundex(?) OR UPPER(firstname) like ? OR UPPER(lastname) like ?)";
			}
		}
		
		List<Person> result = new ArrayList<Person>();
		PreparedStatement s = con.prepareStatement(sql + where);
		int i = 1;
		if (role != null) {
			s.setString(i++, role);
		}
		
		for (String name: names) {
			s.setString(i++, name);
			s.setString(i++, name);
			s.setString(i++, name + "%");
			s.setString(i++, name + "%");
		}
		ResultSet rs = s.executeQuery();
		while (rs.next()) {
			Person p = fetchPerson(rs, rs.getLong("id"));
			result.add(p);
		}
		rs.close();
		s.close();
		return result;
	}
	

	@Override
	public <T extends AbstractEntity> List<T> load(Session session, final String filter, final String roleCode) {
		List<T> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Person> execute(Connection con) throws SQLException {
				return selectPersons(con, filter, roleCode);
			}
		});
		return result;
	}

}

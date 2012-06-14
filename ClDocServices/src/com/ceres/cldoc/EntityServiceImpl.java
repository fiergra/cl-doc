package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Address;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.util.Jdbc;

public class EntityServiceImpl implements IEntityService {

	private static Logger log = Logger.getLogger("EntityService");

	@Override
	public void save(Session session, final Entity entity) {
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

	private void saveAddresses(Connection con, Entity entity)
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

	private void updateEntity(Connection con, Entity entity)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("update Entity set name = ?, type = ? where id = ?");
		s.setString(1, entity.name);
		s.setInt(2, entity.type);
		s.setLong(3, entity.id);
		s.executeUpdate();
		s.close();
	}

	private void insertEntity(Connection con, Entity entity)
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
				.prepareStatement("insert into Person(gender,id,per_id,firstname,lastname,sndx_firstname,sndx_lastname,dateofbirth) values (?,?,?,?,?,soundex(?),soundex(?),?)");
		int i = 1;
		if (person.gender != null) {
			s.setLong(i++, person.gender.id);
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		s.setLong(i++, person.id);
		s.setLong(i++, person.perId);
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		if (person.dateOfBirth != null) {
			s.setDate(i++, new java.sql.Date(person.dateOfBirth.getTime()));
		} else {
			s.setNull(i++, Types.DATE);
		}
		s.executeUpdate();
		s.close();
	}

	private void updatePerson(Connection con, Person person)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("update Person set gender = ?, firstname = ?,lastname = ?, sndx_firstname = soundex(?), sndx_lastname = soundex(?), dateofbirth = ? where id = ?");
		int i = 1;
		if (person.gender != null) {
			s.setLong(i++, person.gender.id);
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		s.setString(i++, person.firstName);
		s.setString(i++, person.lastName);
		if (person.dateOfBirth != null) {
			s.setDate(i++, new java.sql.Date(person.dateOfBirth.getTime()));
		} else {
			s.setNull(i++, Types.DATE);
		}
		s.setLong(i++, person.id);
		s.executeUpdate();
		s.close();
	}

	@Override
	public <T extends Entity> List<T> load(final Session session, final String filter, final String roleCode) {
		List<T> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Person> execute(Connection con) throws SQLException {
				return selectPersons(session, con, filter, roleCode);
			}
		});
		return result;
	}

	
	@SuppressWarnings("unchecked")
	private <T extends Entity> T load(Session session, Connection con, long id) throws SQLException {
		List<Entity> entities = list(session, con, null, null, id);
		return (T) (entities.isEmpty() ? null : entities.get(0));
	}	

	@Override
	@SuppressWarnings("unchecked")
	public List<Entity> list(final Session session, final Integer typeId, final Long id) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Entity> execute(Connection con) throws SQLException {
				return list(session, con, null, typeId, id);
			}
		});
	}	
	
	private Entity fetchEntity(Session session, Entity e, ResultSet rs, String prefix) throws SQLException {
		long entityId = rs.getLong(prefix + "entityId");
		
		if (e == null || !e.id.equals(entityId)) {
			int typeId = rs.getInt(prefix + "type");
			switch (typeId) {
			case Entity.ENTITY_TYPE_PERSON: 
				Person p = fetchPerson(session, rs, prefix, entityId);
				e = p;
			break;
			case Entity.ENTITY_TYPE_ORGANISATION: 
				Organisation o = fetchOrganisation(rs, prefix, entityId);
				e = o;
			break;
			default:
				e = new Entity();
			}
			e.id = entityId;
			e.name = rs.getString(prefix + "name");
			e.type = rs.getInt(prefix + "type");
		}
		Long addressId = rs.getLong(prefix + "addressId");
		if (!rs.wasNull()) {
			Address a = new Address();
			a.id = addressId;
			a.street = rs.getString(prefix + "street");
			a.number = rs.getString(prefix + "number");
			a.postCode = rs.getString(prefix + "postcode");
			a.city = rs.getString(prefix + "city");
			a.co = rs.getString(prefix + "co");
			e.addAddress(a);
		}

		return e;
	}
	
	@SuppressWarnings("unchecked")
	private List<Entity> list(Session session, Connection con, String name, Integer typeId, Long id) throws SQLException {
		List<Entity> result = new ArrayList<Entity>();
		String sql = "select e.id entityId, e.name, e.type, pers.gender, pers.firstname, pers.lastname, pers.dateofbirth, e.type, adr.id addressId, street, number, city, postcode, co from Entity e "
				+ "left outer join Person pers on pers.id = e.id "
				+ "left outer join Organisation orga on orga.id = e.id "
				+ "left outer join Address adr on adr.entity_id = e.id " 
				+ "where 1=1 ";
		
//				+ "order by e.id";
		
		if (typeId != null) {
			sql += " AND e.type = ?";
		}
		if (name != null) {
			sql += " AND UPPER(e.name) like ?";
		}
		if (id != null) {
			sql += " AND e.id = ?";
		}
		PreparedStatement s = con.prepareStatement(sql);
		int i = 1;
		if (typeId != null) {
			s.setLong(i++, typeId);
		}
		if (name != null) {
			s.setString(i++, name.toUpperCase() + "%");
		}
		if (id != null) {
			s.setLong(i++, id);
		}

		ResultSet rs = s.executeQuery();
		Entity e = null;
		
		while (rs.next()) {
			e = fetchEntity(session, e, rs, "");
			if (!result.contains(e)) {
				result.add(e);
			}
		}
		return result;
	}

	private Organisation fetchOrganisation(ResultSet rs, String prefix, long entityId) {
		Organisation o = new Organisation();
		o.id = entityId;
		return o;
	}

	private Person fetchPerson(Session session, ResultSet rs, String prefix, long entityId) throws SQLException {
		Person p = new Person();
		long genderId = rs.getLong(prefix + "gender");
		if (!rs.wasNull()) {
			p.gender = Locator.getCatalogService().load(session, genderId);
		}
		p.firstName = rs.getString(prefix + "firstname");
		p.lastName = rs.getString(prefix + "lastname");
		p.id = entityId;
		p.dateOfBirth = rs.getDate(prefix + "dateofbirth");
		return p;
	}

	@Override
	public <T extends Entity> T load(final Session session, final long id) {
		T e = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public T execute(Connection con) throws SQLException {
				return load(session, con, id);
			}
		});
		return e;
	}

	@Override
	public List<Person> search(final Session session, final String filter) {
		List<Person> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Person> execute(Connection con) throws SQLException {
				return selectPersons(session, con, filter, null);
			}
		});
		
		return result;
	}
	
	
	private List<Person> selectPersons(Session session, Connection con, String filter, String role) throws SQLException {
		Collection<String> names = new ArrayList<String>();
		Collection<Long> ids = new ArrayList<Long>();

		String sql = "select * from Person p ";
		String where = " where 1=1 ";
		
		if (role != null) {
			sql +=  " inner join Assignment a on a.EntityId = p.Id" +
					" inner join Catalog c on a.role = c.Id";
			where += " AND c.code = ?";
		}
		if (filter != null) {
			StringTokenizer st = new StringTokenizer(filter);
			
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
			
			for (Long id : ids) {
				where += " AND (id = ? OR per_id = ?)";
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
		for (Long id : ids) {
			s.setLong(i++, id);
			s.setLong(i++, id);
		}
		
		
		ResultSet rs = s.executeQuery();
		while (rs.next()) {
			Person p = fetchPerson(session, rs, "", rs.getLong("id"));
			result.add(p);
		}
		rs.close();
		s.close();
		return result;
	}
	

	@Override
	public <T extends Entity> List<T> list(final Session session, final int typeId) {
		List<T> result = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Entity> execute(Connection con) throws SQLException {
				return list(session, con, null, typeId, null);
			}
		});
		return result;
	}

	@Override
	public List<EntityRelation> listRelations(final Session session, final Entity entity, final boolean asSubject) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<EntityRelation> execute(Connection con) throws SQLException {
				ArrayList<EntityRelation> result = new ArrayList<EntityRelation>();
				String sql =
						"select er.id relationId, type.id type_id, type.code type_code, type.shorttext type_shorttext, type.text type_text, type.date type_date, type.parent type_parent, " +
						"subject.id subject_entityId, subject.name subject_name, subject.type subject_type," +
						"object.id object_entityId, object.name object_name, object.type object_type " +
						" from EntityRelation er" +
						" inner join Entity subject on subjectID = subject.id" +
						" left outer join Person subjectPers on subjectPers.id = subject.id" +
						" left outer join Organisation subjectOrga on subjectOrga.id = subject.id" +
						" inner join Entity object on objectID = object.id" +
						" left outer join Person objectPers on objectPers.id = object.id" +
						" left outer join Organisation objectOrga on objectOrga.id = object.id" +
						" inner join Catalog type on er.type = type.id" +
						" where 1=1 ";
				
				sql += asSubject ? "AND subject.id = ?" : "AND object.id = ?";
				
				PreparedStatement s = con.prepareStatement(sql);
				s.setLong(1, entity.id);
				
				ResultSet rs = s.executeQuery(); 
				while (rs.next()) {
					EntityRelation er = new EntityRelation();
					er.id = rs.getLong("relationId");
					er.subject = new Entity();
					er.subject.id = rs.getLong("subject_entityId");
					er.subject.type = rs.getInt("subject_type");
					er.subject.name = rs.getString("subject_name");
					er.object = new Entity();
					er.object.id = rs.getLong("object_entityId");
					er.object.type = rs.getInt("object_type");
					er.object.name = rs.getString("object_name");
//					er.subject = fetchEntity(session, null, rs, "subject_");
//					er.object = fetchEntity(session, null, rs, "object_");
					er.type = CatalogServiceImpl.fetchCatalog(rs, "type_");
					
					er.children = listRelations(session, asSubject ? er.object : er.subject, asSubject);
					result.add(er);
				}
				rs.close();
				s.close();
				
				return result;
			}
		});
	}

	@Override
	public List<Entity> list(final Session session, final String criteria, final int typeId) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<Entity> execute(Connection con) throws SQLException {
				return list(session, con, criteria, typeId, null);
			}
		});
	}

	@Override
	public EntityRelation save(Session session, final EntityRelation er) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public EntityRelation execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement(
						"insert into EntityRelation (type, subjectid, objectid) values (?,?,?);", new String[]{"ID"});
				s.setLong(1, er.type.id);
				s.setLong(2, er.subject.id);
				s.setLong(3, er.object.id);
				er.id = Jdbc.exec(s);
				s.close();
				return er;
			}
		});
	}

	@Override
	public void delete(Session session, final EntityRelation er) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement(
						"delete from EntityRelation where id = ?");
				s.setLong(1, er.id);
				s.execute();
				s.close();
				return null;
			}
		});
	}

}

package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.ceres.cldoc.model.Address;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.EntityRelation;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Patient;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.util.Jdbc;

public class EntityServiceImpl implements IEntityService {

	private static Logger log = Logger.getLogger("EntityService");

	@Override
	public void save(Session session, final Entity entity) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				if (entity.getId() == null) {
					insertEntity(con, entity);
					if (entity instanceof Person) {
						insertPerson(con, (Person) entity);
						if (entity instanceof Patient) {
							insertPatient(con, (Patient) entity);
						}
					} else if (entity instanceof Organisation) {
						insertOrganisation(con, (Organisation) entity);
					}
				} else {
					updateEntity(con, entity);
					if (entity instanceof Person) {
						updatePerson(con, (Person) entity);
						if (entity instanceof Patient) {
							updateOrInsertPatient(con, (Patient) entity);
						}
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
				.prepareStatement("update Address set phone = ?, note = ?, street = ?, number = ?, co = ?, postcode = ?, city = ? where id = ?");
		int i = 1;
		s.setString(i++, a.phone);
		s.setString(i++, a.note);
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
						"insert into Address (Entity_id, phone, note, street, number, co, postcode, city) values (?,?,?,?,?,?,?,?)",
						new String[] { "ID" });
		int i = 1;
		s.setLong(i++, a.entity.getId());
		s.setString(i++, a.phone);
		s.setString(i++, a.note);
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
		s.setString(1, entity.getName());
		s.setLong(2, entity.getType());
		s.setLong(3, entity.getId());
		s.executeUpdate();
		s.close();
	}

	private void insertEntity(Connection con, Entity entity)
			throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"insert into Entity (name,type) values (?,?)",
				new String[] { "ID" });
		s.setString(1, entity.getName());
		s.setLong(2, entity.getType());
		entity.setId(Jdbc.exec(s));
		s.close();
	}

	private void insertOrganisation(Connection con, Organisation entity) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"insert into Organisation (id) values (?)");
		s.setLong(1, entity.getId());
		s.execute();
		s.close();
	}

	private void insertPerson(Connection con, Person person)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("insert into Person(gender,id,firstname,lastname,sndx_firstname,sndx_lastname,dateofbirth) values (?,?,?,?,soundex(?),soundex(?),?)");
		int i = 1;
		if (person.gender != null) {
			s.setLong(i++, person.gender.id);
		} else {
			s.setNull(i++, Types.INTEGER);
		}
		s.setLong(i++, person.getId());
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

	private void insertPatient(Connection con, Patient person)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("insert into Patient(id,per_id) values (?,?)");
		int i = 1;
		s.setLong(i++, person.getId());
		s.setLong(i++, person.perId);
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
		s.setLong(i++, person.getId());
		s.executeUpdate();
		s.close();
	}

	private void updateOrInsertPatient(Connection con, Patient person)
			throws SQLException {
		PreparedStatement s = con
				.prepareStatement("update Patient set per_id = ? where id = ?");
		int i = 1;

		s.setLong(i++, person.perId);
		s.setLong(i++, person.getId());
		int rows = s.executeUpdate();
		s.close();
		
		if (rows == 0) {
			insertPatient(con, person);
		}
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
		Long perId = Jdbc.getLong(rs, prefix + "per_Id");
		
		
		if (e == null || !e.getId().equals(entityId)) {
			int typeId = rs.getInt(prefix + "type");
			switch (typeId) {
			case Entity.ENTITY_TYPE_PERSON: 
				Person p;
				if (perId == null) {
					p = fetchPerson(session, null, rs, prefix, entityId);
				} else {
					p = fetchPatient(session, rs, prefix, entityId);
				}
				e = p;
			break;
			case Entity.ENTITY_TYPE_ORGANISATION: 
				Organisation o = fetchOrganisation(rs, prefix, entityId);
				e = o;
			break;
			default:
				e = new Entity();
			}
			e.setId(entityId);
			e.setName(rs.getString(prefix + "name"));
			e.setType(rs.getInt(prefix + "type"));
		}
		Long addressId = rs.getLong(prefix + "addressId");
		if (!rs.wasNull()) {
			Address a = new Address();
			a.id = addressId;
			a.phone = rs.getString(prefix + "phone");
			a.note = rs.getString(prefix + "note");
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
		String sql = "select e.id entityId, e.name, e.type, pers.gender, pat.per_id, pers.firstname, pers.lastname, pers.dateofbirth, e.type, adr.id addressId, phone, note, street, number, city, postcode, co from Entity e "
				+ "left outer join Person pers on pers.id = e.id "
				+ "left outer join Patient pat on pers.id = pat.id "
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
		o.setId(entityId);
		return o;
	}

	private Person fetchPerson(Session session, Person p, ResultSet rs, String prefix, long entityId) throws SQLException {
		if (p == null) {
			p = new Person();
		}
		long genderId = rs.getLong(prefix + "gender");
		if (!rs.wasNull()) {
			p.gender = Locator.getCatalogService().load(session, genderId);
		}
		p.firstName = rs.getString(prefix + "firstname");
		p.lastName = rs.getString(prefix + "lastname");
		p.setId(entityId);
		p.dateOfBirth = rs.getDate(prefix + "dateofbirth");
		return p;
	}

	private Patient fetchPatient(Session session, ResultSet rs, String prefix, long entityId) throws SQLException {
		Patient p = new Patient();
		fetchPerson(session, p, rs, prefix, entityId);
		p.perId = rs.getLong(prefix + "per_id");
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

		String sql = "select pat.id patId, p.id entId, p.*, pat.* from Person p left outer join Patient pat on pat.id = p.id ";
		String where = " where 1=2 ";
		
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
				where += " OR (sndx_firstname = soundex(?) OR sndx_lastname = soundex(?) OR UPPER(firstname) like ? OR UPPER(lastname) like ?)";
			}
			
			for (Long id : ids) {
				where += " OR (per_id = ?)";
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
		}
		
		
		ResultSet rs = s.executeQuery();
		while (rs.next()) {
			Long perId = Jdbc.getLong(rs, "per_Id");
			long entityId = rs.getLong("entId");
			Person p; 
			if (perId == null) {
				p = fetchPerson(session, null, rs, "", entityId);
			} else {
				p = fetchPatient(session, rs, "", entityId);
			}
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
	public List<EntityRelation> listRelations(final Session session, final Entity entity, final boolean asSubject, final Catalog relationType) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<EntityRelation> execute(Connection con) throws SQLException {
				ArrayList<EntityRelation> result = new ArrayList<EntityRelation>();
				String sql =
						"select er.id relationId, type.id type_id, er.startdate, er.enddate, type.code type_code, type.shorttext type_shorttext, type.logical_order type_logical_order, type.text type_text, type.date type_date, type.parent type_parent, type.number1 type_number1, type.number2 type_number2, " +
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
				
				sql += asSubject ? " AND subject.id = ?" : "AND object.id = ? ";
				if (relationType != null) {
					sql += " AND er.type = ? ";
				}
				sql += " order by type.id, " + (!asSubject ? "subject.name" : "object.name") ;
				PreparedStatement s = con.prepareStatement(sql);
				s.setLong(1, entity.getId());
				if (relationType != null) {
					s.setLong(2, relationType.id);
				}
				
				ResultSet rs = s.executeQuery(); 
				while (rs.next()) {
					EntityRelation er = new EntityRelation();
					er.id = rs.getLong("relationId");
					er.subject = new Entity(rs.getLong("subject_entityId"), rs.getInt("subject_type"), rs.getString("subject_name"));
					er.object = new Entity(rs.getLong("object_entityId"), rs.getInt("object_type"), rs.getString("object_name"));
					er.type = CatalogServiceImpl.fetchCatalog(rs, "type_");
					er.startDate = rs.getDate("startdate");
					er.endDate = rs.getDate("enddate");
					
					er.children = listRelations(session, asSubject ? er.object : er.subject, asSubject, relationType);
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

	private void insertER(Connection con, EntityRelation er) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"insert into EntityRelation (type, subjectid, objectid, startdate, enddate) values (?,?,?,?,?);", new String[]{"ID"});
		s.setLong(1, er.type.id);
		s.setLong(2, er.subject.getId());
		s.setLong(3, er.object.getId());
		if (er.startDate != null) {
			s.setDate(4, new java.sql.Date(er.startDate.getTime()));
		} else {
			s.setNull(4, Types.DATE);
		}
		if (er.endDate != null) {
			s.setDate(5, new java.sql.Date(er.endDate.getTime()));
		} else {
			s.setNull(5, Types.DATE);
		}

		er.id = Jdbc.exec(s);
		s.close();
	}
	
	private void updateER(Connection con, EntityRelation er) throws SQLException {
		PreparedStatement s = con.prepareStatement(
				"update EntityRelation set type=?, subjectid=?, objectid=?, startdate=?, enddate=? where id = ?");
		s.setLong(1, er.type.id);
		s.setLong(2, er.subject.getId());
		s.setLong(3, er.object.getId());
		if (er.startDate != null) {
			s.setDate(4, new java.sql.Date(er.startDate.getTime()));
		} else {
			s.setNull(4, Types.DATE);
		}
		if (er.endDate != null) {
			s.setDate(5, new java.sql.Date(er.endDate.getTime()));
		} else {
			s.setNull(5, Types.DATE);
		}
		s.setLong(6, er.id);
		s.execute();
		s.close();
	}
	
	@Override
	public EntityRelation save(Session session, final EntityRelation er) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public EntityRelation execute(Connection con) throws SQLException {
				if (er.id == null) {
					insertER(con, er);
				} else {
					updateER(con, er);
				}
				return er;
			}
		});
	}

	@Override
	public void delete(Session session, final EntityRelation er) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@SuppressWarnings("unchecked")
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

	@Override
	public long getUniqueId(Session session) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Long execute(Connection con) throws SQLException {
				Statement s = con.createStatement();
				ResultSet rs = s.executeQuery("select MAX(PER_ID)+1 newid from Patient");
				rs.next();
				long newId = rs.getLong("newid");
				s.close();
				return newId;
			}
		});
	}

}

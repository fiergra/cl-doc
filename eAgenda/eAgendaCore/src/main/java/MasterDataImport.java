/**
 * Hello world!
 *
 */
public class MasterDataImport {
//
//	static int counter = 0;
//
//	private static void loadHolidays(Connection con, MongoCollection<Holiday> holidayCollection) throws SQLException {
//		PreparedStatement s = con.prepareStatement("SELECT holiday, cty_cd FROM CRF4SMED_REF_BANK_HLD_V HLD"
//				+ "		INNER JOIN CRF4SMED_REF_BANK_HLD_LOC_V HLD_LOC ON hld.rbh_id = hld_loc.rbh_id"
//				+ "		WHERE holiday > ADD_MONTHS(SYSDATE, -1)");
//		ResultSet rs = s.executeQuery();
//		List<Holiday> holidays = new ArrayList<>();
//		while (rs.next()) {
//			Holiday h = new Holiday(rs.getDate("holiday"), rs.getString("cty_cd"));
//			holidays.add(h);
//		}
//		s.close();
//		
//		holidayCollection.drop();
//		holidayCollection.insertMany(holidays);
//	}
//
//	private static void loadPersons(Connection con, MongoCollection<IResource> resources) throws SQLException {
//		PreparedStatement s = con.prepareStatement("SELECT distinct email, u.per_id, no_sysper, firstName, lastName, gender, birth_date, userid "
//				+ " FROM CRF4SMED_HIST_USERIDS_V u " + 
//				" INNER JOIN CRF4SMED_HIST_EMAILS_V e ON e.per_id = u.per_id AND e.dt_fin > SYSDATE AND e.CTX_CD = 'PROF' AND e.SRC_ID = 10" + 
//					" INNER JOIN SMD_PERSONS p ON p.PER_ID = u.per_id " + 
//				" WHERE u.dt_fin > SYSDATE");
//		ResultSet rs = s.executeQuery();
//		while (rs.next()) {
//			String email = rs.getString("email");
//			Person p = new Person(rs.getLong("per_id"), rs.getString("no_sysper"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"), rs.getDate("birth_date"));
//			User u = new User(rs.getString("userid"), email, p);
//			resources.insertOne(u);
//			if (++counter % 1000 == 0) {
//				System.out.println(counter + " inserted.");
//			}
//		}
//		s.close();
//	}
//
//	private static void loadRooms(Connection con, MongoCollection<IResource> resources) throws SQLException {
//		PreparedStatement s = con.prepareStatement("select VIL_CD, BAT_CD || ' ' || ETG_CD || ' ' || loc_cd loc from CRF4SMED_LOCAUX_V where ins_cd = 'COM'");
//		ResultSet rs = s.executeQuery();
//		while (rs.next()) {
//			Room r = new Room(rs.getString("loc"), new City(rs.getString("VIL_CD")));
//			resources.insertOne(r);
//			if (++counter % 1000 == 0) {
//				System.out.println(counter + " inserted.");
//			}
//		}
//		s.close();
//	}
//
//	
//	public static void main(String[] args) throws InterruptedException {
//		
//		MongoAgendaService mas = new MongoAgendaService();
//
//		JDBC.execute(new SimpleSession(), new ITransactional<Void>() {
//
//			@Override
//			public Void execute(Connection con) throws Exception {
//				loadHolidays(con, mas.holidays());
//
//				mas.resources().drop();
//				loadPersons(con, mas.resources());
//				loadRooms(con, mas.resources());
//				
//				return null;
//			}
//
//		}, 123456L);
//		
//		
//		mas.resources().createIndex(new BasicDBObject("searchString", "text"));
//		mas.resources().find(Filters.text("ralph")).forEach((Block<IResource>)r->System.out.println(r.getDisplayName()));
//		
//		mas.close();
//	}
}


//db.persons.find( { $text: { $search: "ralph 5/8/1969" } } , { score: { $meta: "textScore" } }).sort( { score: { $meta: "textScore" } } ).limit(50)
//db.persons.find( {searchString: { $regex :  /fierg/i  } })
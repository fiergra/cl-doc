import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Person;
import eu.europa.ec.digit.eAgenda.Room;
import eu.europa.ec.digit.eAgenda.User;
import eu.europa.ec.digit.simpletx.ITransactional;
import eu.europa.ec.digit.simpletx.JDBC;
import eu.europa.ec.digit.simpletx.SimpleSession;

/**
 * Hello world!
 *
 */
public class App {

	static int counter = 0;

	public static void main(String[] args) {
		
		ClassModel<IResource> cmResource = ClassModel.builder(IResource.class).enableDiscriminator(true).build();
		ClassModel<User> cmUser = ClassModel.builder(User.class).enableDiscriminator(true).build();
		ClassModel<Person> cmPerson = ClassModel.builder(Person.class).enableDiscriminator(true).build();
		ClassModel<Room> cmRoom = ClassModel.builder(Room.class).enableDiscriminator(true).build();

		PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).register(cmResource, cmUser, cmPerson, cmRoom).build();

		
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), 
				CodecRegistries.fromProviders(pojoCodecProvider));//PojoCodecProvider.builder().automatic(true).build()));
		MongoClient mongoClient = new MongoClient("localhost", MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
		MongoDatabase db = mongoClient.getDatabase("mydb");
//		MongoCollection<Campaign> campaigns = db.getCollection("campaigns", Campaign.class);
//		campaigns.drop();
////
////		MongoCollection<Person> persons = db.getCollection("persons", Person.class);
////		persons.drop();
//		
		MongoCollection<IResource> resources = db.getCollection("resources", IResource.class);
//
////		Campaign c = new Campaign();
////		c.name = "TestCampaign";
////		c.description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
////		WorkPattern wp = new WorkPattern();
////		wp.resource = resources.find().first();
////		Day d = new Day();
////		d.addSlot(new Slot(10, 0, 30));
////		d.addSlot(new Slot(10, 30, 30));
////		d.addSlot(new Slot(11, 0, 30, 5));
////		wp.setDay(0, d);
////		c.addWorkPattern(wp);
////		
////		campaigns.insertOne(c);
////		
////		c = campaigns.find().first();
////		c.patterns.get(0).days.get(0).slots.add(new Slot(14, 30, 240));
////		c.patterns.get(0).days.get(0).slots.remove(0);
////		campaigns.replaceOne(Filters.eq("_id", c.id), c);
////		
//		
//		resources.drop();
//		
		JDBC.execute(new SimpleSession(), new ITransactional<Void>() {

			@Override
			public Void execute(Connection con) throws Exception {
				PreparedStatement s = con.prepareStatement("SELECT distinct email, per_id, no_sysper, firstName, lastName, gender, birth_date, userid "
						+ " FROM CRF4SMED_HIST_USERIDS_V u " + 
						" INNER JOIN CRF4SMED_HIST_EMAILS_V e ON e.per_id = u.per_id AND e.dt_fin > SYSDATE AND e.CTX_CD = 'PROF' AND e.SRC_ID = 10" + 
 						" INNER JOIN SMD_PERSONS p ON p.PER_ID = u.per_id " + 
						" WHERE u.dt_fin > SYSDATE");
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					String email = rs.getString("email");
					Person p = new Person(rs.getLong("per_id"), rs.getString("no_sysper"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"), rs.getDate("birth_date"));
					User u = new User(rs.getString("userid"), email, p);
					resources.insertOne(u);
					if (++counter % 1000 == 0) {
						System.out.println(counter + " inserted.");
					}
				}
				s.close();
				return null;
			}
		}, 123456L);
//		
//
//		JDBC.execute(new SimpleSession(), new ITransactional<Void>() {
//
//			@Override
//			public Void execute(Connection con) throws Exception {
//				PreparedStatement s = con.prepareStatement("select VIL_CD, BAT_CD || ' ' || ETG_CD || ' ' || loc_cd loc from CRF4SMED_LOCAUX_V where ins_cd = 'COM'");
//				ResultSet rs = s.executeQuery();
//				while (rs.next()) {
//					Room r = new Room(rs.getString("loc"), new City(rs.getString("VIL_CD")));
//					resources.insertOne(r);
//					if (++counter % 1000 == 0) {
//						System.out.println(counter + " inserted.");
//					}
//				}
//				s.close();
//				return null;
//			}
//		}, 123456L);
//		
//		
//		resources.createIndex(new BasicDBObject("searchString", "text"));
//		resources.find(Filters.text("ralph")).forEach((Block<IResource>)r->System.out.println(r.getDisplayName()));
//		
//		
//
//		
		mongoClient.close();
	}
}


//db.persons.find( { $text: { $search: "ralph 5/8/1969" } } , { score: { $meta: "textScore" } }).sort( { score: { $meta: "textScore" } } ).limit(50)
//db.persons.find( {searchString: { $regex :  /fierg/i  } })
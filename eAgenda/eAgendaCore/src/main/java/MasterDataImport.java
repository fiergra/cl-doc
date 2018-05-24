import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.RenameCollectionOptions;

import eu.europa.ec.digit.eAgenda.City;
import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.MongoAgendaService;
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
public class MasterDataImport {

	static int counter = 0;

	public static void main(String[] args) throws InterruptedException {
		
		MongoAgendaService mas = new MongoAgendaService();
		MongoDatabase db = mas.getDb();
		
		MongoCollection<IResource> newResources = db.getCollection("tempResources", IResource.class);
		newResources.drop();

		JDBC.execute(new SimpleSession(), new ITransactional<Void>() {

			@Override
			public Void execute(Connection con) throws Exception {
				PreparedStatement s = con.prepareStatement("SELECT distinct email, u.per_id, no_sysper, firstName, lastName, gender, birth_date, userid "
						+ " FROM CRF4SMED_HIST_USERIDS_V u " + 
						" INNER JOIN CRF4SMED_HIST_EMAILS_V e ON e.per_id = u.per_id AND e.dt_fin > SYSDATE AND e.CTX_CD = 'PROF' AND e.SRC_ID = 10" + 
 						" INNER JOIN SMD_PERSONS p ON p.PER_ID = u.per_id " + 
						" WHERE u.dt_fin > SYSDATE");
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					String email = rs.getString("email");
					Person p = new Person(rs.getLong("per_id"), rs.getString("no_sysper"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"), rs.getDate("birth_date"));
					User u = new User(rs.getString("userid"), email, p);
					newResources.insertOne(u);
					if (++counter % 1000 == 0) {
						System.out.println(counter + " inserted.");
					}
				}
				s.close();
				return null;
			}
		}, 123456L);
		

		JDBC.execute(new SimpleSession(), new ITransactional<Void>() {

			@Override
			public Void execute(Connection con) throws Exception {
				PreparedStatement s = con.prepareStatement("select VIL_CD, BAT_CD || ' ' || ETG_CD || ' ' || loc_cd loc from CRF4SMED_LOCAUX_V where ins_cd = 'COM'");
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					Room r = new Room(rs.getString("loc"), new City(rs.getString("VIL_CD")));
					newResources.insertOne(r);
					if (++counter % 1000 == 0) {
						System.out.println(counter + " inserted.");
					}
				}
				s.close();
				return null;
			}
		}, 123456L);
		
		
		newResources.createIndex(new BasicDBObject("searchString", "text"));
		newResources.find(Filters.text("ralph")).forEach((Block<IResource>)r->System.out.println(r.getDisplayName()));
		
		MongoCollection<IResource> oldResources = db.getCollection("resources", IResource.class);
		oldResources.renameCollection(new MongoNamespace("mydb.oldResources"), new RenameCollectionOptions().dropTarget(true));
		newResources.renameCollection(new MongoNamespace("mydb.resources"), new RenameCollectionOptions().dropTarget(true));

		mas.close();
	}
}


//db.persons.find( { $text: { $search: "ralph 5/8/1969" } } , { score: { $meta: "textScore" } }).sort( { score: { $meta: "textScore" } } ).limit(50)
//db.persons.find( {searchString: { $regex :  /fierg/i  } })
package eu.europa.ec.digit.eAgenda;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;

import eu.europa.ec.digit.client.i18n.StringResource;

public class MongoAgendaService {

	private MongoDatabase db;
	private MongoClient mongoClient;

	public MongoAgendaService() {
		init();
	}

	private void init() {
		ClassModel<IResource> cmResource = ClassModel.builder(IResource.class).enableDiscriminator(true).build();
		ClassModel<User> cmUser = ClassModel.builder(User.class).enableDiscriminator(true).build();
		ClassModel<Person> cmPerson = ClassModel.builder(Person.class).enableDiscriminator(true).build();
		ClassModel<Room> cmRoom = ClassModel.builder(Room.class).enableDiscriminator(true).build();

		PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).register(cmResource, cmUser, cmPerson, cmRoom).build();
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));
		
		mongoClient = new MongoClient("localhost", MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
		db = mongoClient.getDatabase("mydb");
		
	}

	public void close() {
		mongoClient.close();
	}

	private MongoDatabase getDb() {
		if (db == null) {
			init();
		}
		return db;
	}

	
	private MongoCollection<Appointment> appointments() {
		return getDb().getCollection("appointments", Appointment.class);
	}

	private MongoCollection<IResource> resources() {
		return getDb().getCollection("resources", IResource.class);
	}

	private MongoCollection<Campaign> campaigns() {
		return getDb().getCollection("campaigns", Campaign.class);
	}

	private MongoCollection<StringResource> stringResources() {
		return getDb().getCollection("stringResources", StringResource.class);
	}


	
	
	
	public Campaign findCampaign(String idOrName) {
		ObjectId oid = ObjectId.isValid(idOrName) ? new ObjectId(idOrName) : null;
		
		Bson filter;
		if (oid != null) {
			filter = Filters.eq("_id", oid);
		} else {
			filter = Filters.eq("name", idOrName);
		}
		Campaign c = campaigns().find(filter).first();
		return c;
	}
	
	public List<Campaign> getCampaigns(User owner) {
		List<Campaign> result = new ArrayList<>();

		campaigns().find(Filters.in("owners", owner)).forEach((Block<Campaign>) c -> result.add(c));
		
		return result;
	}

	public void saveCampaign(Campaign c) {
		if (c.id == null) {
			c.id = new ObjectId();
			campaigns().insertOne(c);
		} else {
			campaigns().replaceOne(Filters.eq("_id", c.id), c, new UpdateOptions().upsert(true));
		}
	}

	public void deleteCampaign(Campaign c) {
		if (c.id != null) {
			campaigns().deleteOne(Filters.eq("_id", c.id));
			c.id = null;
		}
	}

	public Appointment saveAppointment(Appointment a) {
		if (a.id == null) {
			a.id = new ObjectId();
			appointments().insertOne(a);
		} else {
			appointments().replaceOne(Filters.eq("_id", a.id), a, new UpdateOptions().upsert(true));
		}
		return a;
	}

	public Appointment deleteAppointment(Appointment a) {
		appointments().deleteOne(Filters.eq("_id", a.id));
		return a;
	}


	public List<User> findPersons(String filter) {
		List<User> persons = new ArrayList<>();
		List<IResource> resources = findResources(filter);
		
		resources.forEach( p -> {
			if (p instanceof User) {
				persons.add((User) p);
			}
		});
		
		return persons;
	}

	public List<Room> findRooms(String filter) {
		List<Room> rooms = new ArrayList<>();
		List<IResource> resources = findResources(filter);
		
		resources.forEach( p -> {
			if (p instanceof Room) {
				rooms.add((Room) p);
			}
		});
		
		return rooms;
	}

	public List<IResource> findResources(String filter) {
		List<IResource> resources = new ArrayList<>();
		resources().find(Filters.text(filter)).projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).limit(25).forEach((Block <IResource>) p -> resources.add(p));
		return resources;
	}

	public List<Appointment> getAppointments(Date d, Date until, IResource host, IResource guest) {
		List<Appointment> result = new ArrayList<>();
		
		DateFormat df = DateFormat.getDateInstance();
		try {
			Date trunc = df.parse(df.format(d));
			if (until == null) {
				until = new Date(trunc.getTime() + 24 * 60 * 60 * 1000);
			}

			Bson filters = Filters.and(Filters.gte("from", trunc), Filters.lt("from", until));
			
			if (host instanceof User) {
				filters = Filters.and(filters, Filters.eq("host.userId", ((User)host).userId));
			} else if (host instanceof Room) {
				filters = Filters.and(filters, Filters.eq("host.name", ((Room)host).name));
			}

			if (guest instanceof User) {
				filters = Filters.and(filters, Filters.eq("guest.userId", ((User)guest).userId));
			}
			
			appointments().find(filters).forEach((Consumer<Appointment>) a -> { result.add(a);});
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	public User getUser(String userName) {
		return (User) resources().find(Filters.eq("userId", userName)).first();
	}

	public void saveStringResource(StringResource sr) {
		stringResources().replaceOne(Filters.eq("key", sr.key), sr, new UpdateOptions().upsert(true));
	}
	
	public HashMap<String, StringResource> getStringResources() {
		HashMap<String, StringResource> stringMap = new HashMap<>();
		stringResources().find().forEach((Consumer<StringResource>)s -> stringMap.put(s.key, s));
		
		return stringMap;
	}

	
	public static void main(String[] args) {
		MongoAgendaService mas = new MongoAgendaService();
		Campaign c = new Campaign();
		mas.campaigns().insertOne(c);
		System.out.println(c.id);
		
		
	}

}

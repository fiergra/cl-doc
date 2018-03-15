package eu.europa.ec.digit.eAgenda;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

public class MongoAgendaService implements EAgendaCoreService {

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
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));// PojoCodecProvider.builder().automatic(true).build()));
		mongoClient = new MongoClient("localhost", MongoClientOptions.builder().writeConcern(new WriteConcern(0).withJournal(true)).codecRegistry(pojoCodecRegistry).build());
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
	
	@Override
	public List<Campaign> getCampaigns() {
		List<Campaign> result = new ArrayList<>();
		campaigns().find().forEach((Block<Campaign>) c -> result.add(c));

		return result;
	}

	private MongoCollection<Appointment> appointments() {
		return getDb().getCollection("appointments", Appointment.class);
	}

	private MongoCollection<IResource> resources() {
		return getDb().getCollection("resources", IResource.class);
	}

	private MongoCollection<Campaign> campaigns() {
		return getDb().getCollection("campaigns", Campaign.class).withWriteConcern(new WriteConcern(1).withJournal(true));
	}

	@Override
	public void saveCampaign(Campaign c) {
		if (c.id == null) {
			campaigns().insertOne(c);
			// todo find out how to make this more efficient...
			c.id = campaigns().find(Filters.eq("name", c.name)).first().id;
			
		} else {
			campaigns().replaceOne(Filters.eq("_id", c.id), c);
		}
	}

	@Override
	public void deleteCampaign(Campaign c) {
		if (c.id != null) {
			campaigns().deleteOne(Filters.eq("_id", c.id));
			c.id = null;
		}
	}

	@Override
	public Appointment saveAppointment(Appointment a) {
		if (a.id == null) {
			appointments().insertOne(a);
		} else {
			appointments().replaceOne(Filters.eq("_id", a.id), a);
		}
		return a;
	}

	public Appointment deleteAppointment(Appointment a) {
		appointments().deleteOne(Filters.eq("_id", a.id));
		a.id = null;
		return a;
	}


	@Override
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

	public List<IResource> findResources(String filter) {
		List<IResource> resources = new ArrayList<>();
		resources().find(Filters.text(filter)).projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).limit(25).forEach((Block <IResource>) p -> resources.add(p));
		return resources;
	}

	public List<Appointment> getAppointments(Date d, IResource host) {
		List<Appointment> result = new ArrayList<>();
		
		DateFormat df = DateFormat.getDateInstance();
		try {
			Date trunc = df.parse(df.format(d));
			Date nextDay = new Date(trunc.getTime() + 24 * 60 * 60 * 1000);

			Bson filters = null;
			if (host instanceof User) {
				filters = Filters.and(Filters.gte("from", trunc), Filters.lt("from", nextDay), Filters.eq("host.userId", ((User)host).userId));
			} else if (host instanceof Room) {
				filters = Filters.and(Filters.gte("from", trunc), Filters.lt("from", nextDay), Filters.eq("host.name", ((Room)host).name));
			}
			
			appointments().find(filters).forEach((Consumer<Appointment>) a -> { result.add(a);});
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	

}

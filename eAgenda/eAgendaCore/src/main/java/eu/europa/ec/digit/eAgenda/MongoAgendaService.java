package eu.europa.ec.digit.eAgenda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.print.Doc;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;

import eu.europa.ec.digit.athena.workflow.WorkflowDefinition;
import eu.europa.ec.digit.athena.workflow.WorkflowState.StateType;
import eu.europa.ec.digit.athena.workflow.WorkflowTransition;
import eu.europa.ec.digit.client.i18n.StringResource;

public class MongoAgendaService {

	public static final String STRING_RESOURCES = "stringResources";
	public static final String CAMPAIGNS = "campaigns";
	public static final String RESOURCES = "resources";
	public static final String WORKFLOWDEFINITIONS = "workflowdefinitions";
	public static final String APPOINTMENTS = "appointments";
	public static final String HOLIDAYS = "holidays";
	private MongoDatabase db;
	private MongoClient mongoClient;

	public MongoAgendaService() throws IOException {
		init();
	}
	
	private static Logger logger = Logger.getLogger("MongoAgendaService");
	
	private void init() {
		ClassModel<IResource> cmResource = ClassModel.builder(IResource.class).enableDiscriminator(true).build();
		ClassModel<User> cmUser = ClassModel.builder(User.class).enableDiscriminator(true).build();
		ClassModel<Person> cmPerson = ClassModel.builder(Person.class).enableDiscriminator(true).build();
		ClassModel<Room> cmRoom = ClassModel.builder(Room.class).enableDiscriminator(true).build();

		PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).register(cmResource, cmUser, cmPerson, cmRoom).build();
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));
		
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mongodb.properties");
			Properties properties = new Properties();
			properties.load(in);
			String userName = properties.getProperty("userName"); 
			String dataBase = properties.getProperty("dataBase"); 
			String adminDataBase = properties.getProperty("adminDataBase"); 
			String passWord = properties.getProperty("passWord"); 
			String host = properties.getProperty("host"); 

			List<ServerAddress> seeds = new ArrayList<ServerAddress>();
			seeds.add(new ServerAddress(host));
		
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(
			    MongoCredential.createCredential(
			        userName, adminDataBase, passWord.toCharArray()
			    )
			);
			
			mongoClient = new MongoClient(seeds, credentials, MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
			db = mongoClient.getDatabase(dataBase);
		} catch (IOException x) {
			logger.severe("error loading mongodb.properties: " + x.getMessage());
			mongoClient = new MongoClient("localhost", MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
			db = mongoClient.getDatabase("mydb");
		}
		
//			List<ServerAddress> seeds = new ArrayList<ServerAddress>();
//			seeds.add( new ServerAddress( "dpetlab0.cc.cec.eu.int"));
//			
//			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
//			credentials.add(
//			    MongoCredential.createCredential(
//			        "eagenda",
//			        "eagenda",
//			        "eagenda".toCharArray()
//			    )
//			);
		
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

	public MongoClient getClient() {
		return mongoClient;
	}
	
	public MongoCollection<Holiday> holidays() {
		return getDb().getCollection(HOLIDAYS, Holiday.class);
	}

	private MongoCollection<Appointment> appointments() {
		return getDb().getCollection(APPOINTMENTS, Appointment.class);
	}

	public MongoCollection<IResource> resources() {
		return getDb().getCollection(RESOURCES, IResource.class);
	}

	private MongoCollection<Campaign> campaigns() {
		return getDb().getCollection(CAMPAIGNS, Campaign.class);
	}

	private MongoCollection<StringResource> stringResources() {
		return getDb().getCollection(STRING_RESOURCES, StringResource.class);
	}


	
	
	
	public Campaign findCampaign(String idOrName) {
//		ObjectId oid = ObjectId.isValid(idOrName) ? new ObjectId(idOrName) : null;
		
		Bson filter;
//		if (oid != null) {
//			filter = Filters.eq("_id", oid);
//		} else {
//			filter = Filters.eq("name", idOrName);
//		}
		filter = Filters.or(Filters.eq("objectId", idOrName), Filters.eq("name", idOrName));
		Campaign c = campaigns().find(filter).first();
		return c;
	}
	
	public List<Campaign> getCampaigns(User owner) {
		List<Campaign> result = new ArrayList<>();

		campaigns().find(Filters.or(Filters.in("owners", owner),Filters.in("roles.admin", owner),Filters.in("roles.owner", owner),Filters.in("roles.operator", owner))).forEach((Block<Campaign>) c -> result.add(c));
		
		return result;
	}

	public void saveCampaign(Campaign c) {
		if (c.objectId == null) {
			c.objectId = new ObjectId().toHexString();
			campaigns().insertOne(c);
		} else {
//			campaigns().replaceOne(Filters.eq("_id", c.objectId), c, new UpdateOptions().upsert(true));
			campaigns().replaceOne(Filters.eq("objectId", c.objectId), c, new UpdateOptions().upsert(true));
		}
	}

	public void deleteCampaign(Campaign c) {
		if (c.objectId != null) {
//			campaigns().deleteOne(Filters.eq("_id", c.objectId));
			campaigns().deleteOne(Filters.eq("objectId", c.objectId));
			c.objectId = null;
		}
	}

	public Appointment findAppointment(String objectId) {
		return appointments().find(Filters.eq("objectId", objectId)).first();
	}
	
	public Appointment saveAppointment(Appointment a) {
		if (a.objectId == null) {
			a.objectId = new ObjectId().toHexString();
			appointments().insertOne(a);
		} else {
			appointments().replaceOne(Filters.eq("objectId", a.objectId), a, new UpdateOptions().upsert(true));
//			appointments().replaceOne(Filters.eq("_id", a.objectId), a, new UpdateOptions().upsert(true));
		}
		return a;
	}

	public Appointment deleteAppointment(Appointment a) {
		appointments().deleteOne(Filters.eq("objectId", a.objectId));
//		appointments().deleteOne(Filters.eq("_id", a.objectId));
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

//	public User findUser(String userId) {
//		IResource user = resources().find(Filters.eq("userId", userId)).first();
//		return user instanceof User ? (User)user : null;
//	}

	public List<IResource> findResources(String filter) {
		List<IResource> resources = new ArrayList<>();
		resources().find(Filters.text(filter)).projection(Projections.metaTextScore("score")).sort(Sorts.metaTextScore("score")).limit(25).forEach((Block <IResource>) p -> resources.add(p));
		return resources;
	}

	public List<Appointment> getAppointments(Date d, Date until, IResource host, IResource guest) {
		List<Appointment> result = new ArrayList<>();
		
		Bson filters = Filters.and(Filters.gte("from", d), Filters.lt("from", until));
		
		if (host instanceof User) {
			filters = Filters.and(filters, Filters.eq("host.userId", ((User)host).userId));
		} else if (host instanceof Room) {
			filters = Filters.and(filters, Filters.eq("host.name", ((Room)host).name));
		}

		if (guest instanceof User) {
			filters = Filters.and(filters, Filters.eq("guest.userId", ((User)guest).userId));
		}
		
		appointments().find(filters).forEach((Consumer<Appointment>) a -> { result.add(a);});
	
		return result;
	}

	public List<Holiday> loadHolidays(String cityCode) {
		List<Holiday> result = new ArrayList<>();
		FindIterable<Holiday> resultSet; 
		
		if (cityCode == null) {
			resultSet = holidays().find(Filters.eq("cityCode", "BRU"));
		} else {
			resultSet = holidays().find(Filters.eq("cityCode", cityCode));
		}
		resultSet.forEach((Block<Holiday>)h -> result.add(h));
		
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
//		MongoAgendaService mas = new MongoAgendaService();
//
////		final TypeToken<List<Request>> requestListTypeToken = new TypeToken<List<Request>>() {
////        };
////
//        final RuntimeTypeAdapterFactory<IResource> typeFactory = RuntimeTypeAdapterFactory
//                .of(IResource.class, "type")
//                .registerSubtype(User.class)
//                .registerSubtype(Person.class)
//                .registerSubtype(Room.class);
//
//        final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();		
//		MongoCollection<Document> collection = mas.db.getCollection("plainJSON");
//		collection.drop();
//		Campaign c = mas.campaigns().find().first();
//		String json = gson.toJson(c);
//		Document doc = Document.parse(json);
//		collection.insertOne(doc);
//		doc = collection.find().first();
//		c = gson.fromJson(doc.toJson(), Campaign.class);
//		
//		JsonTest jt = new JsonTest("asdf", 42l);
//
//		collection = mas.db.getCollection("jsonTest");
//		collection.drop();
//		doc = Document.parse(gson.toJson(jt));
//		collection.insertOne(doc);
//		doc = collection.find().first();
//		jt = gson.fromJson(doc.toJson(), JsonTest.class);
//		
//		
	}

	public MongoDatabase getDatabase() {
		return null;
	}

	public void log(Object ...objects) {
		Gson gson = new Gson();
		Document document = new Document("log", new Date());
		for (Object o:objects) {
			String json = gson.toJson(o);
			if (json != null && !"null".equals(json)) {
				Document subDoc = Document.parse(json);
				document.append(o.getClass().getSimpleName(), subDoc);
			}
		}
		
		db.getCollection("logEntries").insertOne(document);
	}

	public void saveWorkflowDefinition(WorkflowDefinition wDef) {
		Document doc = new Document();
		doc.append("name", wDef.getName())
		.append("initial", wDef.getStates().stream().filter(s -> s.type.equals(StateType.INITIAL)).map(s -> s.name).collect(Collectors.toList()))
		.append("terminal", wDef.getStates().stream().filter(s -> s.type.equals(StateType.TERMINAL)).map(s -> s.name).collect(Collectors.toList()))
		.append("regular", wDef.getStates().stream().filter(s -> s.type.equals(StateType.REGULAR)).map(s -> s.name).collect(Collectors.toList()))
		.append("transitions", wDef.getTransitions().stream().map(t -> new Document().append("action", t.action).append("from", t.fromState.name).append("to", t.toState.name)).collect(Collectors.toList()));
		
		System.out.println(doc.toJson());
		
		MongoCollection<Document> wfs = getDb().getCollection(WORKFLOWDEFINITIONS);
		wfs.replaceOne(Filters.eq("name", wDef.getName()), doc, new UpdateOptions().upsert(true));
	}

	public List<WorkflowDefinition> getWorkflowDefinitions() {
		List<WorkflowDefinition> result = new ArrayList<>();
		MongoCollection<Document> wfs = getDb().getCollection(WORKFLOWDEFINITIONS);
		wfs.find().forEach((Block<Document>)doc -> {
			WorkflowDefinition wDef = new WorkflowDefinition(doc.getString("name"));
			List<Document> transitions = doc.get("transitions", List.class);
			for (Document d:(List<Document>)transitions) {
				wDef.addTransition(new WorkflowTransition(null, d.getString("from"), d.getString("to"), d.getString("action")));
			}
			result.add(wDef);
		});

		return result;
	}



}

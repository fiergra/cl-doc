package com.ceres.cldoc.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ceres.cldoc.client.service.HumanBeingService;
import com.ceres.cldoc.client.service.PersonService;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.Person;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.ceres.cldoc.shared.util.Strings;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HumanBeingServiceImpl extends RemoteServiceServlet implements
		HumanBeingService {

	static {
		ObjectifyService.register(HumanBeing.class);
	}

	public List<HumanBeing> search(String filter) {
		List<HumanBeing> result = new ArrayList<HumanBeing>();
		Objectify ofy = ObjectifyService.begin();
		String transcript = Strings.transcribe(filter);
		
		result.addAll(ofy.query(HumanBeing.class).filter("transcriptLastName >=", transcript).filter("transcriptLastName <", transcript + "\ufffd").list());
		result.addAll(ofy.query(HumanBeing.class).filter("transcriptFirstName >=", transcript).filter("transcriptFirstName <", transcript + "\ufffd").list());

		return result;
	}
	
	
	@Override
	public List<ValueBag> findByString(String filter) {
//		ArrayList<ValueBag> result = new ArrayList<ValueBag>();
//		try {
//			if (filter.length() > 0) {
//				IndexSearcher isearcher = new IndexSearcher(getDirectory(), true); // read-only=true
//				QueryParser parser = new QueryParser(Version.LUCENE_31, "fullName",
//						analyzer);
//				org.apache.lucene.search.Query query = parser
//						.parse(prepare(filter));
//				ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
//				for (int i = 0; i < hits.length; i++) {
//					Document hitDoc = isearcher.doc(hits[i].doc);
//					HumanBeing p = new HumanBeing();
//					p.firstName = hitDoc.getField("firstName").stringValue();
//					p.lastName = hitDoc.getField("lastName").stringValue();
//					p.id = Long.valueOf(hitDoc.get("id"));
//	
//					result.add(ValueBagHelper.convert(p));
//				}
//				isearcher.close();
//			}
//			return result;
//		} catch (Exception iox) {
//			throw new RuntimeException(iox);
//		}
		return null;
	}

//	private Directory getDirectory() throws IOException {
//		if (directory == null) {
//			GWT.log("initializing lucene index...");
//			directory = new RAMDirectory();
//			analyzer = new StandardAnalyzer(Version.LUCENE_31);
//			IndexWriter iwriter = new IndexWriter(directory,
//					new IndexWriterConfig(Version.LUCENE_31, analyzer));
//
//			Objectify ofy = ObjectifyService.begin();
//			QueryResultIterable<HumanBeing> result = ofy.query(HumanBeing.class)
//					.fetch();
//			QueryResultIterator<HumanBeing> iter = result.iterator();
//			while (iter.hasNext()) {
//				HumanBeing person = iter.next();
//				addPersonToIndex(person, iwriter);
//			}
//
//			iwriter.close();
//			GWT.log("lucene index ready.");
//		}
//
//		return directory;
//	}
//
//	private Analyzer analyzer = null;
//	private Directory directory = null;
//
//	private IndexWriter getIndexWriter() throws IOException {
//		return new IndexWriter(getDirectory(), new IndexWriterConfig(
//				Version.LUCENE_31, analyzer));
//	}
//
//	private void addToLuceneIndex(HumanBeing person)
//			throws IOException {
//		IndexWriter iwriter = getIndexWriter();
//		addPersonToIndex(person, iwriter);
//		iwriter.close();
//	}
//
//	private void addPersonToIndex(HumanBeing humanBeing, IndexWriter iwriter)
//			throws IOException {
//		Document doc = new Document();
//		String text = humanBeing.firstName + " " + humanBeing.lastName;
//		doc.add(new Field("fullName", text, Field.Store.YES,
//				Field.Index.ANALYZED));
//		doc.add(new Field("firstName", humanBeing.firstName, Field.Store.YES,
//				Field.Index.NOT_ANALYZED_NO_NORMS));
//		doc.add(new Field("lastName", humanBeing.lastName, Field.Store.YES,
//				Field.Index.NOT_ANALYZED_NO_NORMS));
//		doc.add(new Field("id", String.valueOf(humanBeing.id), Field.Store.YES,
//				Field.Index.NOT_ANALYZED_NO_NORMS));
//		iwriter.addDocument(doc);
//	}
//
//	private void removePersonFromIndex(Person person, IndexWriter iwriter)
//			throws IOException, ParseException {
//		
//		QueryParser parser = new QueryParser(Version.LUCENE_31, "id", analyzer);
//		org.apache.lucene.search.Query query = parser.parse("id:" + person.id);
//		iwriter.deleteDocuments(query);
//	}

	@Override
	public ValueBag findById(Number id) {
		Objectify ofy = ObjectifyService.begin();
		HumanBeing humanBeing = ofy.get(new Key<HumanBeing>(HumanBeing.class, id.longValue()));
		return ValueBagHelper.convert(humanBeing);
	}

	@Override
	public HumanBeing findById(long id) {
		Objectify ofy = ObjectifyService.begin();
		HumanBeing humanBeing = ofy.get(new Key<HumanBeing>(HumanBeing.class, id));
		return humanBeing;
	}

	private static final PersonService personService = new PersonServiceImpl();

	@Override
	public ValueBag save(ValueBag valueBag) {
		HumanBeing person = ValueBagHelper.reconvert(valueBag);
		boolean doInsert = person.id == null;

		valueBag = personService.save(valueBag);

//		if (doInsert) {
//			try {
//				addToLuceneIndex((HumanBeing) ValueBagHelper.reconvert(valueBag));
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		}

		return ValueBagHelper.convert(person);
	}

	@Override
	public void delete(ValueBag valueBag) {
		Person person = ValueBagHelper.reconvert((ValueBag) valueBag);
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(person);
//		try {
//			removePersonFromIndex(person, getIndexWriter());
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}
}

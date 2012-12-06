package com.ceres.cldoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.IActField;

public class LuceneServiceImpl implements ILuceneService {

	private final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
	private Directory index;

	
	private IndexWriter getIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		IndexWriter w = new IndexWriter(getIndex(), config);

		return w;
	}
	
	private File getPath() {
		File path = new File("./lucene");
		path.mkdirs();
		
		return path;
	}

	@Override
	public void deleteIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriter w = getIndexWriter();
		w.deleteAll();
		w.close();
	}

	private byte[] serialize(Entity e) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream sout = new ObjectOutputStream(out);
		sout.writeObject(e);

		return out.toByteArray();
	}
	
	@Override
	public void addToIndex(Entity entity, Act masterData) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, ParseException {
	    if (entity.id != null && masterData.fields != null && !masterData.fields.isEmpty()) {
			IndexWriter w = getIndexWriter();
			// todo: update instead of delete
			w.deleteDocuments(query("id", String.valueOf(entity.id)));
			StringBuffer content = new StringBuffer(entity.getName());
		    Iterator<Entry<String, IActField>> fieldsIter = masterData.fields.entrySet().iterator();

		    Document doc = new Document();
			doc.add(new Field("id", String.valueOf(entity.id), Field.Store.YES, Field.Index.ANALYZED));
			doc.add(new Field("name", entity.getName(), Field.Store.YES, Field.Index.ANALYZED));
			doc.add(new Field("entity", serialize(entity)));
			
			while (fieldsIter.hasNext()) {
		    	Entry<String, IActField> next = fieldsIter.next();
		    	IActField field = next.getValue();
		    	if (field.getType() == IActField.FT_STRING && field.getStringValue() != null) {
		    		doc.add(new Field(field.getName(), field.getStringValue(), Field.Store.YES, Field.Index.ANALYZED));
		    		content.append(" " + field.getStringValue());
		    	}
		    }
			doc.add(new Field("content", content.toString(), Field.Store.NO, Field.Index.ANALYZED));
		    w.addDocument(doc);
		    w.close();
    	}
	}
	

	private Entity getEntity(Document d) throws IOException, ClassNotFoundException {
	      InputStream in = new ByteArrayInputStream(d.getFieldable("entity").getBinaryValue());
	      ObjectInputStream oin = new ObjectInputStream(in);
	      Entity e = (Entity) oin.readObject();
	      
	      return e;
	}
	
	@Override
	public List<Entity> retrieve(String criteria) throws CorruptIndexException, IOException, ParseException, ClassNotFoundException {
	    IndexSearcher searcher = new IndexSearcher(getIndexReader());
		ScoreDoc[] hits = search(searcher, criteria);
		List<Entity> result = new ArrayList<Entity>(hits.length);

		for(int i = 0; i < hits.length; ++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
//		  HashMap<String, Object> record = new HashMap<String, Object>();
//	      for (Fieldable f:d.getFields()) {
//	    	  if (f instanceof Field) {
//	    		  Field field = (Field)f;
//	    		  record.put(field.name(), field.stringValue());
//	    	  }
//	      }
	      result.add(getEntity(d));
	    }

	    // searcher can only be closed when there
	    // is no need to access the documents any more. 
	    searcher.close();		
		
		return result;
	}

	private Query query(String fieldName, String criteria) throws ParseException {
		return new QueryParser(Version.LUCENE_36, fieldName, analyzer).parse(criteria);
	}
	
	private ScoreDoc[] search(IndexSearcher searcher, String criteria) throws CorruptIndexException, IOException, ParseException, ClassNotFoundException {
		Query q = query("content", criteria);
	    int hitsPerPage = 10;
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    return hits;
	}	    

	private IndexReader getIndexReader() throws CorruptIndexException, IOException {
		return IndexReader.open(getIndex());
	}

	private Directory getIndex() throws IOException {
		if (index == null) {
			index = new SimpleFSDirectory(getPath());
		}
		return index;
	}

}

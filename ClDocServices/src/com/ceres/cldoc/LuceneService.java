package com.ceres.cldoc;

import java.io.File;
import java.io.IOException;
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

public class LuceneService implements ILuceneService {

	private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
	private Directory index;

	
	private IndexWriter getIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		IndexWriter w = new IndexWriter(getIndex(), config);

		return w;
	}
	
	private File getPath() {
		File path = new File(".");
		path.mkdir();
		
		return path;
	}

	@Override
	public void deleteIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriter w = getIndexWriter();
		w.close();
	}
	
	@Override
	public void addToIndex(Entity entity, Act masterData) throws CorruptIndexException, LockObtainFailedException, IOException {
	    if (entity.id != null) {
			IndexWriter w = getIndexWriter();
		    Document doc = new Document();
		    StringBuffer content = new StringBuffer(entity.name);
		    Iterator<Entry<String, IActField>> fieldsIter = masterData.fields.entrySet().iterator();
		    
			doc.add(new Field("id", String.valueOf(entity.id), Field.Store.NO, Field.Index.ANALYZED));
			doc.add(new Field("name", entity.name, Field.Store.NO, Field.Index.ANALYZED));
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
	

	@Override
	public List<Entity> retrieve(String criteria) throws CorruptIndexException, IOException, ParseException {
		Query q = new QueryParser(Version.LUCENE_36, "content", analyzer).parse(criteria);

	    // 3. search
	    int hitsPerPage = 10;
	    IndexReader reader = IndexReader.open(getIndex());
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    
	    // 4. display results
	    System.out.println("Found " + hits.length + " hits.");
	    for(int i=0;i<hits.length;++i) {
	      int docId = hits[i].doc;
	      Document d = searcher.doc(docId);
	      System.out.println((i + 1) + ". " + d.get("name"));
	    }

	    // searcher can only be closed when there
	    // is no need to access the documents any more. 
	    searcher.close();		
		
		return null;
	}

	private Directory getIndex() throws IOException {
		if (index == null) {
			index = new SimpleFSDirectory(getPath());
		}
		return index;
	}

}

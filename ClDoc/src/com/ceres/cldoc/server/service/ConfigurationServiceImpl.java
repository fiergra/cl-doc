package com.ceres.cldoc.server.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ceres.cldoc.client.service.ConfigurationService;
import com.ceres.cldoc.shared.domain.Assignment;
import com.ceres.cldoc.shared.domain.Catalog;
import com.ceres.cldoc.shared.domain.FormClassDesc;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConfigurationServiceImpl extends RemoteServiceServlet implements
		ConfigurationService {

	static {
		ObjectifyService.register(FormClassDesc.class);
		ObjectifyService.register(Catalog.class);
	}
	
	@Override
	public List<String> listChildren(String parent) {
		ArrayList<String> result = new ArrayList<String>();
		if (parent.equals("MAIN")) {
			result.add("HOME");
			result.add("CONFIG");
			result.add("DEBUG");
		} else if (parent.equals("PERSONALFILE")) {
			result.add("HISTORY");
			result.add("DETAILS");
		}
		return result;
	}

	private static BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	@Override
	public String getUploadUrl() {
		return blobstoreService.createUploadUrl("/cldoc/uploadService");
	}

	@Override
	public LayoutElement parse(String xml) {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		LayoutElement result = null;
		try {
			XMLStreamReader parser = factory.createXMLStreamReader(new StringReader(xml));
			boolean eod = false;

			if (parser.getEventType() == XMLStreamConstants.START_DOCUMENT) {
				result = new LayoutElement("DOC");
			
				while (!eod) {
					int event = parser.next();
	
					if (event == XMLStreamConstants.END_DOCUMENT) {
						eod = true;
					} else if (event == XMLStreamConstants.START_DOCUMENT) {
					} else if (event == XMLStreamConstants.END_ELEMENT) {
						result = result.getParent();
					} else if (event == XMLStreamConstants.START_ELEMENT) {
						LayoutElement child = new LayoutElement(parser.getLocalName());
						int acount = parser.getAttributeCount();
						
						for (int i = 0; i < acount; i++) {
							String localName = parser.getAttributeLocalName(i);
							String value = parser.getAttributeValue(null, localName);
							
							child.setAttribute(localName, value);
						}
						result.addChild(child);
						result = child;
					}
				}
			}
			parser.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<FormClassDesc> listClasses(FormClassDesc parent, String filter) {
		Objectify ofy = ObjectifyService.begin();
		Query<FormClassDesc> query = ofy.query(FormClassDesc.class);
		
		List<FormClassDesc> result = (filter != null && filter.length() > 0)? query.filter("name >=", filter).filter("name <", filter + "\ufffd").list() : query.list();
		return result;
	}

	@Override
	public void saveLayoutDesc(String formClass, String xmlLayout) {
		Objectify ofy = ObjectifyService.begin();
		
		FormClassDesc fc = ofy.find(FormClassDesc.class, formClass);
		if (fc == null) {
			fc = new FormClassDesc();
			fc.name = formClass;
		}
		
		fc.xmlLayout = xmlLayout;
		ofy.put(fc);
	}

	@Override
	public FormClassDesc getFormClassDesc(String className) {
		Objectify ofy = ObjectifyService.begin();

		return ofy.get(FormClassDesc.class, className);
	}

	@Override
	public void deleteFormClassDesc(String className) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(FormClassDesc.class, className);
	}

	@Override
	public void delete(Catalog catalog) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(catalog);
	}
	
	@Override
	public Catalog save(Catalog catalog) {
		Objectify ofy = ObjectifyService.begin();
		ObjectifyFactory ofa = ofy.getFactory();
		updateParentKey(ofa, catalog);
		ofy.put(catalog);
		return catalog;
	}
	
	@Override
	public Catalog getCatalog(String code) {
		Objectify ofy = ObjectifyService.begin();
		Key<Catalog> key = new Key<Catalog>(Catalog.class, code);
		return ofy.get(key);
	}

	@Override
	public List<Catalog> listCatalogs(String parentCode) {
		Catalog parent = getCatalog(parentCode);
		return listCatalogs(parent);
	}
	
	@Override
	public List<Catalog> listCatalogs(Catalog parent) {
		Objectify ofy = ObjectifyService.begin();
		ObjectifyFactory ofa = ObjectifyService.factory();
		
		List<Catalog> result = new ArrayList<Catalog>();
		QueryResultIterator<Catalog> iter;
		
		if (parent != null) {
			Key<Object> parentKey = ofa.getKey(parent);
			iter = ofy.query(Catalog.class).ancestor(parentKey).iterator();

			while (iter.hasNext()) {
				Catalog c = iter.next();
				if (!ofa.getKey(c).equals(parentKey)) {
					c.parentCatalog = parent;
					result.add(c);
				}
			}
			
		} else {
			iter = ofy.query(Catalog.class).iterator();
			HashMap<Key<Object>, Catalog> catalogHash = new HashMap<Key<Object>, Catalog>();

			while (iter.hasNext()) {
				Catalog c = iter.next();
				catalogHash.put(ofa.getKey(c), c);
				
				if (c.parent == null) {
					result.add(c);
				} else {
					c.parentCatalog = catalogHash.get(c.parent);
					c.parentCatalog.addChild(c);
				}
			}
		}
//		List<Catalog> result = query.filter("parent =", parent).list();
		
		return result;
	}

	@Override
	public void saveAll(Collection<Catalog> catalogs) {
		Objectify ofy = ObjectifyService.begin();
		updateParentKeys(ofy, catalogs);
		ofy.put(catalogs);
	}

	private void updateParentKey(ObjectifyFactory ofa, Catalog catalog) {
		if (catalog.parentCatalog != null) {
			catalog.parent = ofa.getKey(catalog.parentCatalog);
		}
	}

	
	private void updateParentKeys(Objectify ofy, Collection<Catalog> catalogs) {
		ObjectifyFactory ofa = ofy.getFactory();
		for (Catalog c : catalogs) {
			updateParentKey(ofa, c);
		}
	}

	@Override
	public Assignment addAssignment(Catalog catalog, RealWorldEntity entity) {
		Objectify ofy = ObjectifyService.begin();
		ObjectifyFactory ofa = ofy.getFactory();
		
		Assignment assignment = new Assignment();
		assignment.pkCatalog = ofa.getKey(catalog);
		assignment.pkEntity = ofa.getKey(entity);
		ofy.put(assignment);
		
		return assignment;
	}

	@Override
	public List<Assignment> listAssignments(Catalog catalog) {
		Objectify ofy = ObjectifyService.begin();
		ObjectifyFactory ofa = ofy.getFactory();
		Key <Catalog> key = ofa.getKey(catalog);
		
		QueryResultIterator<Assignment> iter = ofy.query(Assignment.class).filter("pkCatalog =", key).iterator();
		return fetchAssignments(ofy, iter);
	}

	private List<Assignment> fetchAssignments(Objectify ofy, QueryResultIterator<Assignment> iter) {
		List<Assignment> result = new ArrayList<Assignment>();
		while (iter.hasNext()) {
			Assignment assignment = iter.next();
			assignment.entity = ofy.get(assignment.pkEntity);
			assignment.catalog = ofy.get(assignment.pkCatalog);
			result.add(assignment);
		}
		
		return result;
	}
	
	@Override
	public List<Assignment> listAssignments(RealWorldEntity entity) {
		Objectify ofy = ObjectifyService.begin();
		ObjectifyFactory ofa = ofy.getFactory();
		Key <RealWorldEntity> key = ofa.getKey(entity);
		
		QueryResultIterator<Assignment> iter = ofy.query(Assignment.class).filter("pkEntity =", key).iterator();
		return fetchAssignments(ofy, iter);
	}

}

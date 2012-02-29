package com.ceres.cldoc.server.service;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ceres.cldoc.ICatalogService;
import com.ceres.cldoc.ILayoutDefinitionService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.ConfigurationService;
import com.ceres.cldoc.model.AbstractEntity;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConfigurationServiceImpl extends RemoteServiceServlet implements
		ConfigurationService {

//	private static BlobstoreService blobstoreService = BlobstoreServiceFactory
//			.getBlobstoreService();
//
//	@Override
//	public String getUploadUrl() {
//		String uploadURL = blobstoreService.createUploadUrl("/cldoc/uploadService");
//		uploadURL = uploadURL.replace("0.0.0.0", "127.0.0.1");
//		
//		return uploadURL;
//	}

	@Override
	public LayoutElement parse(Session session, String xml) {
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
	public List<LayoutDefinition> listLayoutDefinitions(Session session, String filter) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		return lds.listLayoutDefinitions(session, filter);
	}

	@Override
	public void saveLayoutDefinition(Session session, String formClass, String xmlLayout) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		LayoutDefinition ld = new LayoutDefinition();
		ld.name = formClass;
		ld.xmlLayout = xmlLayout;
		lds.save(session, ld);
	}

	@Override
	public LayoutDefinition getLayoutDefinition(Session session, String className) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		return lds.load(session, className);
	}

	@Override
	public void deleteLayoutDefinition(Session session, String className) {
	}

	@Override
	public void delete(Session session, Catalog catalog) {
		getCatalogService().delete(session, catalog);
	}
	
	@Override
	public Catalog save(Session session, Catalog catalog) {
		return catalog;
	}
	
	private ICatalogService getCatalogService() {
		return Locator.getCatalogService();
	}
	
	@Override
	public Catalog getCatalog(Session session, long id) {
		return getCatalogService().load(session, id);
	}

	@Override
	public Collection<Catalog> listCatalogs(Session session, String parentCode) {
		return getCatalogService().loadList(session, parentCode);
	}
	

	@Override
	public Collection<Catalog> listCatalogs(Session session, Long parentId) {
		return listCatalogs(session, parentId != null ? new Catalog(parentId) : null);
	}
	
	@Override
	public Collection<Catalog> listCatalogs(Session session, Catalog parent) {
		return getCatalogService().loadList(session, parent);
	}

	@Override
	public void saveAll(Session session, Collection<Catalog> catalogs) {
		for (Catalog c:catalogs) {
			getCatalogService().save(session, c);
		}
	}

	@Override
	public Assignment addAssignment(Session session, Catalog catalog, AbstractEntity entity) {
		Assignment assignment = new Assignment();
		return assignment;
	}

	@Override
	public List<Assignment> listAssignments(Session session, Catalog catalog) {
		return null;
	}

	@Override
	public List<Assignment> listAssignments(Session session, AbstractEntity entity) {
		return null;
	}

}
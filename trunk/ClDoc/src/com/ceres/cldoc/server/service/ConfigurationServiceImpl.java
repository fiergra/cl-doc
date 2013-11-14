package com.ceres.cldoc.server.service;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ceres.cldoc.IActService;
import com.ceres.cldoc.ICatalogService;
import com.ceres.cldoc.IDocArchive;
import com.ceres.cldoc.ILayoutDefinitionService;
import com.ceres.cldoc.IReportService;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.service.ConfigurationService;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Assignment;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.FileSystemNode;
import com.ceres.cldoc.model.IAct;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.ReportDefinition;
import com.ceres.cldoc.shared.layout.LayoutElement;
import com.ceres.core.ISession;
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
	public LayoutElement parse(ISession session, String xml) {
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
	public List<ActClass> listClasses(ISession session, String filter) {
		IActService actService = Locator.getActService();
		return actService.listClasses(session, filter);
	}

//	@Override
//	public List<LayoutDefinition> listLayoutDefinitions(ISession session, String filter) {
//		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
//		return lds.listLayoutDefinitions(session, filter);
//	}

	@Override
	public LayoutDefinition saveLayoutDefinition(ISession session, LayoutDefinition ld) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		lds.save(session, ld);
		return ld;
	}

	@Override
	public LayoutDefinition getLayoutDefinition(ISession session, String className, int typeId) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		return lds.load(session, className, typeId);
	}

	@Override
	public List<LayoutDefinition> listLayoutDefinitions(ISession session, int typeId, Long entityType, Boolean isSingleton) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		return lds.listLayoutDefinitions(session, null, typeId, entityType, isSingleton);
	}
	
	@Override
	public void deleteLayoutDefinition(ISession session, String className) {
		ILayoutDefinitionService lds = Locator.getLayoutDefinitionService();
		lds.delete(session, className);
	}

	@Override
	public void delete(ISession session, Catalog catalog) {
		getCatalogService().delete(session, catalog);
	}
	
	@Override
	public Catalog save(ISession session, Catalog catalog) {
		return catalog;
	}
	
	private ICatalogService getCatalogService() {
		return Locator.getCatalogService();
	}
	
	private IReportService getReportService() {
		return Locator.getReportService();
	}
	
	private IActService getActService() {
		return Locator.getActService();
	}

	
//	private ILuceneService getLuceneService() {
//		return Locator.getLuceneService();
//	}
	
	private IDocArchive getDocArchive() {
		return Locator.getDocArchive();
	}
	
	@Override
	public Catalog getCatalog(ISession session, long id) {
		return getCatalogService().load(session, id);
	}

	@Override
	public Catalog getCatalog(ISession session, String code) {
		return getCatalogService().load(session, code);
	}

	@Override
	public List<Catalog> listCatalogs(ISession session, String parentCode) {
		return getCatalogService().loadList(session, parentCode);
	}
	

	@Override
	public List<Catalog> listCatalogs(ISession session, Long parentId) {
		return listCatalogs(session, parentId != null ? new Catalog(parentId) : null);
	}
	
	@Override
	public List<Catalog> listCatalogs(ISession session, Catalog parent) {
		return getCatalogService().loadList(session, parent);
	}

	@Override
	public void saveAll(ISession session, Collection<Catalog> catalogs) {
		for (Catalog c:catalogs) {
			getCatalogService().save(session, c);
		}
	}

	@Override
	public Assignment addAssignment(ISession session, Catalog catalog, Entity entity) {
		Assignment assignment = new Assignment();
		return assignment;
	}

	@Override
	public List<Assignment> listAssignments(ISession session, Catalog catalog) {
		return null;
	}

	@Override
	public List<Assignment> listAssignments(ISession session, Entity entity) {
		return null;
	}

	@Override
	public List<ReportDefinition> listReportDefinitions(ISession session) {
		return getReportService().list(session, null);
	}

	@Override
	public ReportDefinition loadReportDefinition(ISession session, Catalog catalog) {
		return getReportService().load(session, catalog);
	}

	@Override
	public List<HashMap<String, Serializable>> executeReport(ISession session,
			ReportDefinition rd, IAct filters) {
		return getReportService().execute(session, rd, filters);
	}

//	@Override
//	public String getLuceneIndexPath() {
//		return getLuceneService().getIndexPath().getAbsolutePath();
//	}
//
//	@Override
//	public void setLuceneIndexPath(ISession session, String path) {
//		File indexPath = new File(path);
//		indexPath.mkdirs();
//		getLuceneService().setIndexPath(indexPath);
//		getActService().rebuildIndex(session);
//	}

	@Override
	public String getDocArchivePath() {
		return getDocArchive().getArchivePath().getAbsolutePath();
	}

	@Override
	public void setDocArchivePath(ISession session, String path) {
		File archivePath = new File(path);
		archivePath.mkdirs();
		getDocArchive().setArchivePath(archivePath);
	}

	@Override
	public List<FileSystemNode> listFiles(String directory) {
		return Locator.getSettingsService().listFiles(directory);
	}

	@Override
	public void set(ISession session, String name, String value, Entity entity) {
		Locator.getSettingsService().set(session, name, value, entity);
	}

	@Override
	public String get(ISession session, String name, Entity entity) {
		return Locator.getSettingsService().get(session, name, entity);
	}

}

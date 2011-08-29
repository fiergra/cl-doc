package com.ceres.cldoc.server.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ceres.cldoc.client.service.ConfigurationService;
import com.ceres.cldoc.shared.layout.FormDesc;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConfigurationServiceImpl extends RemoteServiceServlet implements
		ConfigurationService {

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
	public FormDesc parse(String xml) {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		FormDesc result = null;
		try {
			XMLStreamReader parser = factory.createXMLStreamReader(new StringReader(xml));
			boolean eod = false;

			if (parser.getEventType() == XMLStreamConstants.START_DOCUMENT) {
				result = new FormDesc(parser.getLocalName());
			
				while (!eod) {
					int event = parser.next();
	
					if (event == XMLStreamConstants.END_DOCUMENT) {
						eod = true;
					} else if (event == XMLStreamConstants.START_DOCUMENT) {
					} else if (event == XMLStreamConstants.END_ELEMENT) {
						result = result.getParent();
					} else if (event == XMLStreamConstants.START_ELEMENT) {
						FormDesc child = new FormDesc(parser.getLocalName());
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

}

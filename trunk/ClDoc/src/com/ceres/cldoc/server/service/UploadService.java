package com.ceres.cldoc.server.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.Participation;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.core.client.GWT;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

//The FormPanel must submit to a servlet that extends HttpServlet  
//RemoteServiceServlet cannot be used
@SuppressWarnings("serial")
public class UploadService extends HttpServlet {
	static {
		ObjectifyService.register(GenericItem.class);
		ObjectifyService.register(Participation.class);
	}

	// Start Blobstore and Objectify Sessions
	BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	// Override the doPost method to store the Blob's meta-data
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("fup");

		GenericItem vb = new GenericItem("externalDoc", null);
		String rweKey = req.getParameter("rweKey");
		
		vb.set("fileName", req.getParameter("fileName"));
		vb.set("blob-key", blobKey.getKeyString());
		vb.set("url", "/cldoc/uploadService?blob-key=" + blobKey.getKeyString());
		
		GWT.log("upload blob-key: " + blobKey.getKeyString());
		
		Objectify ofy = ObjectifyService.begin();
		ofy.put(vb);
		
		if (rweKey != null) {
			Participation p = new Participation();
			p.pkEntity = new Key<RealWorldEntity>(RealWorldEntity.class, Long.valueOf(rweKey));
			p.pkValueBag = new Key<GenericItem>(GenericItem.class, vb.getId());
			ofy.put(p);
		}
		
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
//		resp.setContentType("video/3gp");
		GWT.log("serve blob-key: " + blobKey.getKeyString());
		blobstoreService.serve(blobKey, resp);

	}

	
}

package com.ceres.cldoc.server.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.ceres.cldoc.IDocArchive;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.service.UserService;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ActClass;
import com.ceres.cldoc.model.Attachment;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.Session;

//The FormPanel must submit to a servlet that extends HttpServlet  
//RemoteServiceServlet cannot be used
@SuppressWarnings("serial")
public class UploadService extends HttpServlet {
	
	private Session getSession(HttpServletRequest req) {
		Session session = (Session) req.getSession().getAttribute(UserService.CLDOC_SESSION);
		return session;
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (ServletFileUpload.isMultipartContent(req)) {
			// Create a factory for disk-based file acts
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			Session session = getSession(req);
			String type = req.getParameter("type");
			try {
				List<FileItem> items = upload.parseRequest(req);

				if ("form_layouts".equals(type) || "print_layouts".equals(type)) {
					for (FileItem fItem : items) {
						if (!fItem.isFormField()) {
							String fileName = fItem.getName();
							if (fileName != null) {
								Locator.getLayoutDefinitionService().importLayouts(session, fItem.getInputStream());
							}							
						}
					}
				} else if ("catalogs".equals(type)) {
					for (FileItem fItem : items) {
						if (!fItem.isFormField()) {
							String fileName = fItem.getName();
							if (fileName != null) {
								Locator.getCatalogService().importXML(session, fItem.getInputStream());
							}							
						}
					}
				} else if (ActClass.EXTERNAL_DOC.name.equals(type)) {
					Long entityId = null;
					Long actId = null;
					Attachment attachment = new Attachment();
					
					for (FileItem fItem : items) {
						// process only file upload - discard other form act
						// types
						if (fItem.isFormField()) {
							if (fItem.getFieldName().equals("entityId")) {
								String sId = fItem.getString();
								entityId = Long.valueOf(sId);
							} else if (fItem.getFieldName().equals("actId")) {
								String sId = fItem.getString();
								actId = Long.valueOf(sId);
							} else if (fItem.getFieldName().equals("comment")) {
								String value = fItem.getString();
								if (value != null && value.length() > 0) {
									attachment.description = value;
								}
							}
						} else {
							String fileName = fItem.getName();
							// get only the file name not whole path
							if (fileName != null) {
								
								fileName = FilenameUtils.getName(fileName);
								attachment.filename = fileName;
//								act.set("fileName", fileName);
								IDocArchive docarc = Locator.getDocArchive();
								String path = Locator.getSettingsService().get(session, IDocArchive.DOC_ARCHIVE_PATH, null);
								if (path != null) {
									docarc.setArchivePath(new File(path));
								}
								attachment.docId = docarc.store(fileName, fItem.getInputStream(), null);
//								act.set("docId", docId);
							}
						}
					}

					if (entityId != null) {
						Act act = new Act(ActClass.EXTERNAL_DOC);
						act.set("fileName", attachment.filename);
						act.set("docId", attachment.docId);
						act.set("comment", attachment.description);
						Entity entity = Locator.getEntityService().load(session, entityId);
						act.setParticipant(entity, Participation.PROTAGONIST, new Date(), null);
						act.date = new Date();
						Locator.getActService().save(session, act);
					} else if (actId != null) {
						attachment.act = new Act();
						attachment.act.id = actId;
						Locator.getActService().saveAttachment(session, attachment);
					}
				}
			} catch (Exception e) {
				resp.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"An error occurred while creating the file : "
								+ e.getMessage());
			}
		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
		}
	}

	private byte[] copyBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int read = inputStream.read(buffer);

		while (read > 0) {
			out.write(buffer, 0, read);
			read = inputStream.read(buffer);
		}

		return out.toByteArray();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

}

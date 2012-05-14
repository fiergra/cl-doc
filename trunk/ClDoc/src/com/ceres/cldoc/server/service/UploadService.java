package com.ceres.cldoc.server.service;

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

import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;

//The FormPanel must submit to a servlet that extends HttpServlet  
//RemoteServiceServlet cannot be used
@SuppressWarnings("serial")
public class UploadService extends HttpServlet {
	
	private Session getSession(HttpServletRequest req) {
		String sSid = (String) req.getParameter("sid");
		String sUid = (String) req.getParameter("uid");
		Session session = null;
		
		if (sSid != null && sUid != null) {
			User user = new User();
			user.id = Long.valueOf(sUid);
			session = new Session(user, Long.valueOf(sSid));
		}

		return session;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (ServletFileUpload.isMultipartContent(req)) {
			// Create a factory for disk-based file acts
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			Session session = getSession(req);
			String type = (String) req.getParameter("type");
			try {
				List<FileItem> items = upload.parseRequest(req);

				if ("form_layouts".equals(type) || "print_layouts".equals(type)) {
					for (FileItem fItem : items) {
						if (!fItem.isFormField()) {
							String fileName = fItem.getName();
							if (fileName != null) {
								Locator.getLayoutDefinitionService().importZip(session, getTypeLayoutType(type), fItem.getInputStream());
							}							
						}
					}
				} else if ("externalDoc".equals(type)) {
					Act act = new Act("externalDoc");
					Long entityId = null;
					for (FileItem fItem : items) {
						// process only file upload - discard other form act
						// types
						if (fItem.isFormField()) {
							if (fItem.getFieldName().equals("entityId")) {
								String sId = fItem.getString();
								entityId = Long.valueOf(sId);
							} else if (fItem.getFieldName().equals("userId")) {

							} else {
								act.set(fItem.getFieldName(), fItem.getString());
							}
						} else {
							String fileName = fItem.getName();
							// get only the file name not whole path
							if (fileName != null) {
								
								fileName = FilenameUtils.getName(fileName);
								act.set("fileName", fileName);
								act.set("file", copyBytes(fItem.getInputStream()));
							}
						}
					}
					Person person = Locator.getEntityService().load(session, entityId);
					act.addParticipant(person, Catalog.PATIENT, new Date(), null);
					act.date = new Date();
					Locator.getActService().save(session, act);
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

	private int getTypeLayoutType(String type) {
		if ("form_layouts".equals(type)) { return LayoutDefinition.FORM_LAYOUT; }
		if ("print_layouts".equals(type)) { return LayoutDefinition.PRINT_LAYOUT; }
		return -1;
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

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

}
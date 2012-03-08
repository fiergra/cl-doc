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
import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;

//The FormPanel must submit to a servlet that extends HttpServlet  
//RemoteServiceServlet cannot be used
@SuppressWarnings("serial")
public class UploadService extends HttpServlet {
	// Override the doPost method to store the Blob's meta-data
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (ServletFileUpload.isMultipartContent(req)) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				GenericItem item = new GenericItem("externalDoc");
				Long entityId = null;
				List<FileItem> items = upload.parseRequest(req);
				for (FileItem fItem : items) {
					// process only file upload - discard other form item types
					if (fItem.isFormField()) { 
						if (fItem.getFieldName().equals("entityId")) {
							String sId = fItem.getString();
							entityId = Long.valueOf(sId);
						} else if (fItem.getFieldName().equals("userId")) {
							
						} else {
							item.set(fItem.getFieldName(), fItem.getString());
						}
					} else {
						String fileName = fItem.getName();
						// get only the file name not whole path
						if (fileName != null) {
							fileName = FilenameUtils.getName(fileName);
							item.set("fileName", fileName);
							item.set("file", copyBytes(fItem.getInputStream()));
						}
					}
				}
				Session session = new Session(new User());
				Person person = Locator.getEntityService().load(session, entityId);
				item.addParticipant(person, new Date(), null);
				item.date = new Date();
				Locator.getGenericItemService().save(session, item);
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

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

}

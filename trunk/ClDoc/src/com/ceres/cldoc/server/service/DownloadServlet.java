package com.ceres.cldoc.server.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;

import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.client.service.UserService;
import com.ceres.cldoc.model.Act;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	public static HashMap<String, ByteArrayOutputStream> files = new HashMap<String, ByteArrayOutputStream>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final String type = req.getParameter("type");
		byte[] out = null;
		Session session = (Session) req.getSession().getAttribute(UserService.CLDOC_SESSION);
		
		if (session != null) {
			if ("form_layouts".equals(type)) {
				resp.setContentType("application/zip");
				out = Locator.getLayoutDefinitionService().exportZip(session);
//			} else if ("print_layouts".equals(type)) {
//				resp.setContentType("application/zip");
//				out = Locator.getLayoutDefinitionService().exportZip(session, LayoutDefinition.PRINT_LAYOUT);
			} else if ("catalogs".equals(type)) {
				resp.setContentType("text/xml");
				String xml = Locator.getCatalogService().exportXML(session, null);
				out = xml.getBytes("UTF-8");
			} else if ("xsl".equals(type)) {
				resp.setContentType("application/x-msexcel");
				final String reportId = req.getParameter("id");
				if (reportId != null) {
					Log.warn("FILTERS ARE NOT YET SUPPORTED!!!");
					out = Locator.getReportService().exportXLS(session, Long.valueOf(reportId), null);
				}
			} else if ("pdf".equals(type)) {
				final String sid = req.getParameter("id");
				final long id = Long.valueOf(sid);
				Act act = Locator.getActService().load(session, id);
				out = Locator.getDocService().print(session, act);
			} else {
				String fileName = req.getParameter("file");
				if (fileName != null) {
					File file = new File(fileName);
					InputStream in = new FileInputStream(file);
					byte[] bytes = new byte[4096];
					int read = in.read(bytes);
					while (read > 0) {
						resp.getOutputStream().write(bytes, 0, read);
						read = in.read(bytes);
					}
					in.close();
					file.delete();
				} else {
					final String id = req.getParameter("id");
					long docId = Long.parseLong(id);
					fileName = Locator.getDocArchive().getFileName(docId);
					out = Locator.getDocArchive().retrieve(docId);
				}
				String mimeType;
				
				if (fileName.toLowerCase().endsWith(".pdf")) {
					mimeType = "application/pdf";
				} else {
					mimeType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fileName);
				}
				resp.setContentType(mimeType + "; name=" + fileName + "\nContent-Disposition: attachment; filename=" + fileName + "\n\n");
			}
		}
		if (out != null) {
			resp.getOutputStream().write(out);
		} else {
			resp.getOutputStream().write(("invalid key or type. ").getBytes());
		}

	}
	
}

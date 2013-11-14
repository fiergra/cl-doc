package com.ceres.cldoc.server.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ceres.cldoc.IDocArchive;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.client.service.UserService;
import com.ceres.cldoc.model.Act;
import com.ceres.core.ISession;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	public static HashMap<String, ByteArrayOutputStream> files = new HashMap<String, ByteArrayOutputStream>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final String type = req.getParameter("type");
		byte[] out = null;
		ISession session = (ISession) req.getSession().getAttribute(UserService.CLDOC_SESSION);
		
		if (session != null) {
			if ("form_layouts".equals(type)) {
//				resp.setContentType("application/zip");
//				out = Locator.getLayoutDefinitionService().exportZip(session);
				resp.setContentType("text/xml");
				String xml = Locator.getLayoutDefinitionService().exportLayouts(session);
				out = xml.getBytes("UTF-8");
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
					Logger.getAnonymousLogger().warning("FILTERS ARE NOT YET SUPPORTED!!!");
					out = Locator.getReportService().exportXLS(session, Long.valueOf(reportId), null);
				}
			} else if ("pdf".equals(type)) {
				final String sid = req.getParameter("id");
				final long id = Long.valueOf(sid);
				Act act = Locator.getActService().load(session, id);
				out = Locator.getDocService().print(session, act);
			} else if ("timesheet".equals(type)) {
				final String sUserId = req.getParameter("userid");
				final String sMonth = req.getParameter("month");
				out = (sUserId + " " + sMonth).getBytes();
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
					
					IDocArchive archive = Locator.getDocArchive();
					String path = Locator.getSettingsService().get(session, IDocArchive.DOC_ARCHIVE_PATH, null);
					if (path != null) {
						archive.setArchivePath(new File(path));
					}
					
					fileName = archive.getFileName(docId);
					out = archive.retrieve(docId);
				}
				String mimeType;
				
				if (fileName.toLowerCase().endsWith(".pdf")) {
					mimeType = "application/pdf";
				} else if (fileName.toLowerCase().endsWith(".jpg")){
					mimeType = "image/jpeg";
				} else if (fileName.toLowerCase().endsWith(".png")){
					mimeType = "image/png";
				} else if (fileName.toLowerCase().endsWith(".tiff")){
					mimeType = "image/tiff";
				} else if (fileName.toLowerCase().endsWith(".gif")){
					mimeType = "image/gif";
				} else if (fileName.toLowerCase().endsWith(".odt")){
					mimeType = "application/vnd.oasis.opendocument.text";
				} else if (fileName.toLowerCase().endsWith(".odp")){
					mimeType = "application/vnd.oasis.opendocument.presentation";
				} else if (fileName.toLowerCase().endsWith(".ods")){
					mimeType = "application/vnd.oasis.opendocument.spreadsheet";
				} else {
					mimeType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fileName);
					resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				}
				resp.setContentType(mimeType);
			}
		}
		if (out != null) {
			resp.getOutputStream().write(out);
		} else {
			resp.getOutputStream().write(("invalid key or type. ").getBytes());
		}

	}
	
}

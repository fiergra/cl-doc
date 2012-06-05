package com.ceres.cldoc.server.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			} else if ("pdf".equals(type)) {
				final String sid = req.getParameter("id");
				final long id = Long.valueOf(sid);
				Act act = Locator.getActService().load(session, id);
				out = Locator.getDocService().print(session, act);
			} else {
				resp.setContentType("application/pdf");
				final String fileName = req.getParameter("file");
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
					
					out = Locator.getDocArchive().retrieve(docId);
//					
//					out = Jdbc.doTransactional(session,
//							new ITransactional() {
//	
//								@Override
//								public byte[] execute(Connection con)
//										throws SQLException {
//									PreparedStatement s = con
//											.prepareStatement("select blobvalue from ActField where id = ?");
//									s.setString(1, id);
//									ResultSet rs = s.executeQuery();
//									byte[] bytes = null;
//									if (rs.next()) {
//										bytes = rs.getBytes("blobvalue");
//									}
//									rs.close();
//									s.close();
//	
//									return bytes;
//								}
//							});
				}
			}
		}
		if (out != null) {
			resp.getOutputStream().write(out);
		} else {
			resp.getOutputStream().write(("invalid key or type. ").getBytes());
		}

	}

	private void fromIn2Out(InputStream in, OutputStream out) throws IOException {
		byte[] bytes = new byte[4096];
		int read = in.read(bytes);
		while (read > 0) {
			out.write(bytes, 0, read);
			read = in.read(bytes);
		}
		in.close();

	}
	
}

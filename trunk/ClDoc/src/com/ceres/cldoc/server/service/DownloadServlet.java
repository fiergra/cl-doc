package com.ceres.cldoc.server.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.util.Jdbc;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {

	public static HashMap<String, ByteArrayOutputStream> files = new HashMap<String, ByteArrayOutputStream>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final String type = req.getParameter("type");
		byte[] out = null;
		Session session = new Session();
		
		if ("form_layouts".equals(type)) {
			resp.setContentType("application/zip");
			out = Locator.getLayoutDefinitionService().exportZip(session, LayoutDefinition.FORM_LAYOUT);
		} else if ("print_layouts".equals(type)) {
			resp.setContentType("application/zip");
			out = Locator.getLayoutDefinitionService().exportZip(session, LayoutDefinition.PRINT_LAYOUT);
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
				out = Jdbc.doTransactional(session,
						new ITransactional() {

							@Override
							public byte[] execute(Connection con)
									throws SQLException {
								PreparedStatement s = con
										.prepareStatement("select blobvalue from ActField where id = ?");
								s.setString(1, id);
								ResultSet rs = s.executeQuery();
								byte[] bytes = null;
								if (rs.next()) {
									bytes = rs.getBytes("blobvalue");
								}
								rs.close();
								s.close();

								return bytes;
							}
						});
			}
		}
		if (out != null) {
			resp.getOutputStream().write(out);
		} else {
			resp.getOutputStream().write(("invalid key or type. ").getBytes());
		}

	}

}

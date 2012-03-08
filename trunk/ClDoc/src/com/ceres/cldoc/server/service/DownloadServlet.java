package com.ceres.cldoc.server.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.ceres.cldoc.Session;
import com.ceres.cldoc.util.Jdbc;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet {
	
	public static HashMap<String, ByteArrayOutputStream> files = new HashMap<String, ByteArrayOutputStream>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		final String id = req.getParameter("id");
		Session session = new Session();
		byte[] out = Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public byte[] execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement("select blobvalue from ItemField where id = ?");
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

		if (out != null) {
			resp.setContentType("application/pdf");
			resp.getOutputStream().write(out);
		} else {
			resp.getOutputStream().write(("invalid key: " + id).getBytes());
		}
	}

}

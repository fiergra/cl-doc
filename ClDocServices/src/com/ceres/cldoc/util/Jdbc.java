package com.ceres.cldoc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.TxManager;
import com.ceres.cldoc.model.ISession;

public class Jdbc {

	private static Logger log = Logger.getLogger("Jdbc");

	public static Long exec(PreparedStatement s) throws SQLException {
		int rows = s.executeUpdate();
		log.finer(rows + " affected.");
		ResultSet keys = s.getGeneratedKeys();
		Long id = null;
		if (keys.next()) {
			id = keys.getLong(1);
		}
		keys.close();
		return id;
	}

	public static <T> T doTransactional(ISession session, ITransactional itransactional) {
		try {
			Connection con = TxManager.start(session);
			T result = itransactional.execute(con);
			TxManager.end(session);
			return result;
		} catch (RuntimeException rx) {
			TxManager.cancel(session);
			throw rx;
		} catch (Exception x) {
			log.severe(x.getLocalizedMessage());
			TxManager.cancel(session);
			throw new RuntimeException(x);
		}
		
	}

	public static Long getLong(ResultSet rs, String columnLabel) throws SQLException {
		long value = rs.getLong(columnLabel);
		return rs.wasNull() ? null : value;
	}

}
